package com.seanthomascarroll.jmediator;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DefaultRequestHandlerProvider implements RequestHandlerProvider {

    private final Map<Class<?>, RequestHandler<? extends Request, ?>> handlers = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Request, R> RequestHandler<T, R> getRequestHandler(T request) {
        return (RequestHandler<T, R>) handlers.get(request.getClass());
    }

    /**
     * @param handler
     * @param <T>
     * @throws ClassNotFoundException
     */
    @Override
    public <T extends Request> void register(RequestHandler<? super T, ?> handler) {
        Ensure.notNull(handler);
        Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handler.getClass(), RequestHandler.class);
        handlers.putIfAbsent(requestClass, handler);
    }

}
