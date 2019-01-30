package jmediator.dropwizard;

import io.github.classgraph.ClassGraph;
import jmediator.*;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.internal.inject.InjectionManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandlerProviderImpl implements RequestHandlerProvider, ServletContextListener {

    private final ClassGraph classGraph;
//    private ServiceLocator serviceLocator;
//    private InjectionManager injectionManager;
    private Map<Class<?>, String> handlers = new HashMap<>();

    public RequestHandlerProviderImpl(InjectionManager injectionManager, String... packagesToScan) {
        this.classGraph = new ClassGraph().whitelistPackages(packagesToScan);
        //this.injectionManager = injectionManager;
    }

    public RequestHandlerProviderImpl(String... packagesToScan) {
        this.classGraph = new ClassGraph().whitelistPackages(packagesToScan);
        //this.serviceLocator = ServiceLocatorFactory.getInstance().create("RequestHandlerProviderImpl");
    }

    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request) {
        String handlerClassName = handlers.get(request.getClass());

        //RequestHandler<Request, Object> handler = ServiceLocatorUtilities.getService(serviceLocator, handlerClassName);
        // should we just assert not null?
//        if (handler == null) {
//            throw new NoHandlerForRequestException("request handler not found for class " + request.getClass());
//        }
//        return handler;
        return null;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        handlers.clear();
        List<String> requestHandlerNames = getRequestHandlerClassNames();
        for (String className : requestHandlerNames) {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(clazz, RequestHandler.class);
                // we only want to store the class name as the actual handler should be managed by HK2 and could have
                // custom lifecycle or scope depending on how it added to the injection binder
                handlers.putIfAbsent(requestClass, className);
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
