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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class QuarkusServiceFactory implements ServiceFactory {
    private static final Logger LOGGER = Logger.getLogger(QuarkusServiceFactory.class);

    private final Map<String, Class<? extends RequestHandler>> handlerClassNames;
    private final List<Class<? extends PipelineBehavior>> behaviors;
    private final ArcContainer container;

    public QuarkusServiceFactory(Map<String, Class<? extends RequestHandler>> handlerClassNames, List<Class<? extends PipelineBehavior>> behaviors) {
        this(handlerClassNames, behaviors, Arc.container());
    }

    QuarkusServiceFactory(Map<String, Class<? extends RequestHandler>> handlerClassNames,
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

        // TODO: Will this cause a memory leak if we dont close?
        InstanceHandle<? extends RequestHandler> instance = container.instance(handlerClassName);

//        if (instance.isAvailable()) {
//
//        }

        RequestHandler handler = instance.get();
        if (handler == null) {
            throw new NoHandlerForRequestException(requestClass);
        }

        return handler;
    }

    @Override
    public List<PipelineBehavior> getPipelineBehaviors() {
        try {
            List<PipelineBehavior> pipelineBehaviors = new ArrayList<>();
            for (Class<? extends PipelineBehavior> clazz : behaviors) {
                // TODO: Will this cause a memory leak if we dont close?
                InstanceHandle<? extends PipelineBehavior> instance = container.instance(clazz);
                PipelineBehavior behavior = instance.get();
                if (behavior != null) {
                    pipelineBehaviors.add(behavior);
                }
            }
            return pipelineBehaviors;
        } catch (Exception ex) {
            throw new ServiceFactoryException("Failed to create PipelineBehavior bean(s)", ex);
        }
    }
}
