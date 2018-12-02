package jmediator.dropwizard;

import jmediator.*;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandlerProviderImpl implements RequestHandlerProvider {

    private final ServiceLocator serviceLocator;

    private Map<Class<?>, RequestHandler<Request, Object>> handlers = new HashMap<>();


    public RequestHandlerProviderImpl() {
        this(ServiceLocatorUtilities.createAndPopulateServiceLocator());
    }

    public RequestHandlerProviderImpl(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
        registerHandlers();
    }

    private void registerHandlers() {
        List<RequestHandler> requestHandlers = serviceLocator.getAllServices(RequestHandler.class);
        for (RequestHandler handler : requestHandlers) {
            Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handler.getClass(), RequestHandler.class);
            handlers.putIfAbsent(requestClass, handler);
        }
    }

//    @Override
//    public <T extends Request, R> RequestHandler<T, R> getRequestHandler(T request) {
//        return null;
//    }

    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request) {
        RequestHandler<Request, Object> handler = handlers.get(request.getClass());
        if (handler == null) {
            throw new NoHandlerForRequestException("request handler not found for class " + request.getClass());
        }
        return handler;
    }
}
