package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.NoHandlerForRequestException;
import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.ServiceFactory;
import com.seanthomascarroll.jmediator.ServiceFactoryException;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import io.quarkus.arc.Arc;
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

    private final Map<String, Class<RequestHandler>> handlerClassNames;
    private final List<Class<PipelineBehavior>> behaviors;

    public QuarkusServiceFactory(Map<String, Class<RequestHandler>> handlerClassNames, List<Class<PipelineBehavior>> behaviors) {
        this.handlerClassNames = handlerClassNames;
        this.behaviors = behaviors;
    }

    @Override
    public <T extends Request, R> RequestHandler<T, R> getRequestHandler(Class<? extends Request> requestClass) {
        Class<RequestHandler> handlerClassName = handlerClassNames.get(requestClass.getName());
        if (handlerClassName == null) {
            throw new NoHandlerForRequestException(requestClass);
        }

        InstanceHandle<RequestHandler> instance = Arc.container().instance(handlerClassName);
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
            for (Class<PipelineBehavior> clazz : behaviors) {
                InstanceHandle<PipelineBehavior> instance = Arc.container().instance(clazz);
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