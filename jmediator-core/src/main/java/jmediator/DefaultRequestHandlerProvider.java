package jmediator;

import java.util.HashMap;
import java.util.Map;

public class DefaultRequestHandlerProvider implements RequestHandlerProvider {

    private Map<Class<?>, RequestHandler<? extends Request, ?>> handlers = new HashMap<>();

    @Override
    public RequestHandler<? extends Request, ?> getRequestHandler(Request request) {
        return handlers.get(request.getClass());
    }

    public <T extends Request> void register(RequestHandler<? super T, ?> handler) throws ClassNotFoundException {
        Class<?> handlerClass = Class.forName(handler.getClass().getName());
        Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handlerClass, RequestHandler.class);
        handlers.putIfAbsent(requestClass, handler);
    }
}
