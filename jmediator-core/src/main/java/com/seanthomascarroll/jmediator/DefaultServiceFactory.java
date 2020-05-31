package com.seanthomascarroll.jmediator;

import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultServiceFactory implements ServiceFactory {

    private final Map<Class<?>, RequestHandler<? extends Request, ?>> handlers = new HashMap<>();
    private final List<PipelineBehavior> behaviors = new ArrayList<>();

    /**
     * @param handler
     * @param <T>
     * @throws ClassNotFoundException
     */
    public <T extends Request> void register(RequestHandler<? super T, ?> handler) {
        Ensure.notNull(handler);
        Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handler.getClass(), RequestHandler.class);
        handlers.putIfAbsent(requestClass, handler);
    }

    /**
     * @param behavior
     * @throws ClassNotFoundException
     */
    public void register(PipelineBehavior behavior) {
        behaviors.add(Ensure.notNull(behavior));
    }

    @Override
    public <T extends Request, R> RequestHandler<T, R> getRequestHandler(Class<? extends Request> requestClass) {
        return (RequestHandler<T, R>) handlers.get(requestClass);
    }

    @Override
    public List<PipelineBehavior> getPipelineBehaviors() {
        return behaviors;
    }
}
