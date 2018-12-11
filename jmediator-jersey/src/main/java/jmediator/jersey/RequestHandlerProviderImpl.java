package jmediator.jersey;

import io.github.classgraph.ClassGraph;
import jmediator.*;
import org.glassfish.jersey.internal.inject.InjectionManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Registry a better name?
// https://github.com/cloudogu/command-bus/blob/develop/command-bus-spring/src/main/java/de/triology/cb/spring/Registry.java
public class RequestHandlerProviderImpl implements RequestHandlerProvider, ServletContextListener {

    private final String[] packagesToScan;
    private final InjectionManager injectionManager;
    private Map<Class<?>, Class<RequestHandler>> handlerClassNames = new HashMap<>();

    public RequestHandlerProviderImpl(InjectionManager injectionManager, String... packagesToScan) {
        this.injectionManager = injectionManager;
        this.packagesToScan = packagesToScan;
    }

    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request) {
        Class<RequestHandler> requestHandlerClassName = handlerClassNames.get(request.getClass());
        RequestHandler<Request, Object> handler = injectionManager.getInstance(requestHandlerClassName);
        if (handler == null) {
            throw new NoHandlerForRequestException("request handler not found for class " + request.getClass());
        }
        return handler;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // TODO: just do this as part of the constructor?
        handlerClassNames.clear();
        List<String> requestHandlersNames = serviceNames(packagesToScan);
        for (String requestHandlersName : requestHandlersNames) {
            try {
                Class<RequestHandler> handlerClass = (Class<RequestHandler>) Class.forName(requestHandlersName);
                Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handlerClass, RequestHandler.class);
                // we only want to store the class name as the actual handler should be managed by Jersey's injection
                // framework and could have custom lifecycle or scope depending on how it added to the injection binder
                handlerClassNames.putIfAbsent(requestClass, handlerClass);
            } catch (ClassNotFoundException e) {
                throw new NoHandlerForRequestException("request handler not found for class " + requestHandlersName, e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private static List<String> serviceNames(String... packages) {
        return new ClassGraph().whitelistPackages(packages)
            .scan()
            .getClassesImplementing(RequestHandler.class.getName())
            .getNames();
    }

}
