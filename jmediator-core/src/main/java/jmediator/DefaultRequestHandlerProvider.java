package jmediator;

import java.util.HashMap;
import java.util.Map;

public class DefaultRequestHandlerProvider implements RequestHandlerProvider {

    private Map<Class<?>, RequestHandler> handlers = new HashMap<>();

    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request) {
        return handlers.get(request.getClass());
    }

    public void register(RequestHandler<?, ?> handler) throws ClassNotFoundException {
        Class<?> handlerClass = Class.forName(handler.getClass().getName());
        Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handlerClass, RequestHandler.class);
        handlers.putIfAbsent(requestClass, handler);
    }
}
