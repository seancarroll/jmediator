package jmediator.dropwizard;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.jetty.MutableServletContextHandler;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.github.classgraph.ClassGraph;
import jmediator.*;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.servlet.ServletProperties;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JmediatorBundle<T extends Configuration> implements ConfiguredBundle<T>, RequestHandlerProvider {

    private final ClassGraph classGraph;
    private final Map<String, String> handlerClassNames = new HashMap<>();
    private MutableServletContextHandler contextHandler;
    private ServiceLocator serviceLocator;

    public JmediatorBundle(String... packagesToScan) {
        this.classGraph = new ClassGraph().whitelistPackages(packagesToScan);
    }

    @Override
    public void run(T t, Environment environment) throws Exception {
        Collection<RequestHandler> rhs = environment.getApplicationContext().getBeans(RequestHandler.class);
        Collection<RequestHandler> rhs2 = environment.getApplicationContext().getContainedBeans(RequestHandler.class);

//        final ServiceLocator locator = (ServiceLocator) environment.getJerseyServletContainer().getServletConfig().getServletContext()
//            .getAttribute(ServletProperties.SERVICE_LOCATOR);
//        ServiceLocator locator = (ServiceLocator) environment.getJerseyServletContainer().getServletConfig().getServletContext().getAttribute(ServletProperties.SERVICE_LOCATOR);
//        serviceLocator = (ServiceLocator) environment.getApplicationContext().getAttributes().getAttribute(ServletProperties.SERVICE_LOCATOR);

        contextHandler = environment.getApplicationContext();

        List<String> requestHandlerNames = getRequestHandlerClassNames();
        for (String className : requestHandlerNames) {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(clazz, RequestHandler.class);
                // we only want to store the class name as the actual handler should be managed by HK2 and could have
                // custom lifecycle or scope depending on how it added to the injection binder
                handlerClassNames.putIfAbsent(requestClass.getName(), className);
            } catch (ClassNotFoundException e) {
                throw new NoHandlerForRequestException("request handler not found for class " + className, e);
            }
        }


        RequestHandlerProvider provider = this;

        // https://github.com/eclipse-ee4j/jersey/issues/3675
        // This currently doesn't work...org.glassfish.hk2.utilities.binding.AbstractBinder
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                RequestDispatcherImpl dispatcher = new RequestDispatcherImpl(provider);
                bind(dispatcher).to(RequestDispatcher.class);

//                for (Class clazz : handlers.values()) {
//                    bind(clazz).to(clazz);
//                }
            }
        });


//        //InjectionManagerProvider.getInjectionManager()
//        // InjectionManager injectionManager = ((ServletContainer) environment.getJerseyServletContainer()).getApplicationHandler().getInjectionManager();
//        // environment.getApplicationContext().
//        for (RequestHandler requestHandler : environment.getApplicationContext().getBeans(RequestHandler.class)) {
//            try {
//                Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(requestHandler.getClass(), RequestHandler.class);
//                // we only want to store the class name as the actual handler should be managed by Spring and could have
//                // custom lifecycle or scope depending on how it added to the injection binder
//                handlerClassNames.putIfAbsent(requestClass.getName(), beanName);
//            } catch (ClassNotFoundException e) {
//                throw new NoHandlerForRequestException("request handler not found for class " + beanName, e);
//            }
//        }


    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }

    private List<String> getRequestHandlerClassNames() {
        return classGraph
            .scan()
            .getClassesImplementing(RequestHandler.class.getName())
            .getNames();
    }

    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request) {
        try {
            String handlerClass = handlerClassNames.get(request.getClass().getName());
//        RequestHandler<Request, Object> handler = injectionManager.getInstance(handlerClass);
            RequestHandler<Request, Object> handler = (RequestHandler<Request, Object>) contextHandler.getBean(Class.forName(handlerClass));
            if (handler == null) {
                throw new NoHandlerForRequestException("request handler not found for class " + request.getClass());
            }
            return handler;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
