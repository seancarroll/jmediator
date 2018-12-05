package jmediator.dropwizard;

import io.github.classgraph.ClassGraph;
import jmediator.*;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandlerProviderImpl implements RequestHandlerProvider, ServletContextListener {

    private final ClassGraph classGraph;
    private final ServiceLocator serviceLocator;
    private Map<Class<?>, RequestHandler<Request, Object>> handlers = new HashMap<>();

    public RequestHandlerProviderImpl(String... packagesToScan) {
        this.classGraph = new ClassGraph().whitelistPackages(packagesToScan);
        this.serviceLocator = ServiceLocatorFactory.getInstance().create("RequestHandlerProviderImpl");
    }

    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request) {
        RequestHandler<Request, Object> handler = handlers.get(request.getClass());
        if (handler == null) {
            throw new NoHandlerForRequestException("request handler not found for class " + request.getClass());
        }
        return handler;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        handlers.clear();
        List<String> requestHandlerNames = getRequestHandlerClassNames();
        for (String className : requestHandlerNames) {
            try {
                Class<?> clazz = Class.forName(className);
                RequestHandler<Request, Object> handler = ServiceLocatorUtilities.getService(serviceLocator, className);
                if (handler == null) {
                    throw new NoHandlerForRequestException("request handler not found for class " + className);
                }
                Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(clazz, RequestHandler.class);
                handlers.putIfAbsent(requestClass, handler);
            } catch (ClassNotFoundException e) {
                throw new NoHandlerForRequestException("request handler not found for class " + className, e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private List<String> getRequestHandlerClassNames() {
        return classGraph
            .scan()
            .getClassesImplementing(RequestHandler.class.getName())
            .getNames();
    }

}
