package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.NoHandlerForRequestException;
import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.ServiceFactory;
import com.seanthomascarroll.jmediator.ServiceFactoryException;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;
import io.quarkus.arc.InstanceHandle;
import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class QuarkusServiceFactory implements ServiceFactory {
    private static final Logger LOGGER = Logger.getLogger(QuarkusServiceFactory.class);

    private final Map<String, Class<? extends RequestHandler<?, ?>>> handlerClassNames;
    private final List<Class<? extends PipelineBehavior>> behaviors;
    private final ArcContainer container;
    private final Map<Object, InstanceHandle<?>> destroyableInstances = new IdentityHashMap<>();

    public QuarkusServiceFactory(Map<String, Class<? extends RequestHandler<?, ?>>> handlerClassNames, List<Class<? extends PipelineBehavior>> behaviors) {
        this(handlerClassNames, behaviors, Arc.container());
    }

    QuarkusServiceFactory(Map<String, Class<? extends RequestHandler<?, ?>>> handlerClassNames,
                          List<Class<? extends PipelineBehavior>> behaviors,
                          ArcContainer container) {
        this.handlerClassNames = handlerClassNames;
        this.behaviors = behaviors;
        this.container = container;
    }

    @Override
    public <T extends Request, R> RequestHandler<T, R> getRequestHandler(Class<? extends Request> requestClass) {
        Class<? extends RequestHandler> handlerClassName = handlerClassNames.get(requestClass.getName());
        if (handlerClassName == null) {
            throw new NoHandlerForRequestException(requestClass);
        }

        InstanceHandle<? extends RequestHandler> handle = container.instance(handlerClassName);
        RequestHandler handler = handle.get();
        if (handler == null) {
            throw new NoHandlerForRequestException(requestClass);
        }

        if (handle.getBean().getScope().equals(Dependent.class)) {
            destroyableInstances.put(handler, handle);
        }
        return handler;
    }

    @Override
    public List<PipelineBehavior> getPipelineBehaviors() {
        try {
            List<PipelineBehavior> pipelineBehaviors = new ArrayList<>();
            for (Class<? extends PipelineBehavior> clazz : behaviors) {
                InstanceHandle<? extends PipelineBehavior> handle = container.instance(clazz);
                PipelineBehavior behavior = handle.get();
                if (behavior != null) {
                    pipelineBehaviors.add(behavior);
                    if (handle.getBean().getScope().equals(Dependent.class)) {
                        destroyableInstances.put(behavior, handle);
                    }
                }
            }
            return pipelineBehaviors;
        } catch (Exception ex) {
            throw new ServiceFactoryException("Failed to create PipelineBehavior bean(s)", ex);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void release(List<Object> instance) {
        for (Object key : instance) {
            InstanceHandle handle = destroyableInstances.remove(key);
            if (handle != null) {
                handle.destroy();
            }
        }
    }
}
