package jmediator.jersey;

import io.github.classgraph.ClassGraph;
import jmediator.*;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandlerProviderImpl implements RequestHandlerProvider, ServletContextListener {

//    private ConfigurableListableBeanFactory beanFactory;
private ServiceLocator serviceLocator;
    private Map<Class<?>, RequestHandler<Request, Object>> handlers = new HashMap<>();
//
//    public RequestHandlerProviderImpl(ConfigurableListableBeanFactory beanFactory) {
//        this.beanFactory = beanFactory;
//    }

    private String[] packagesToScan;

    public RequestHandlerProviderImpl(String... packagesToScan) {
        this.packagesToScan = packagesToScan;
        this.serviceLocator = ServiceLocatorFactory.getInstance().create("uniqueName");
    }

    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request) {
        RequestHandler<Request, Object> handler = handlers.get(request.getClass());
        if (handler == null) {
            throw new NoHandlerForRequestException("request handler not found for class " + request.getClass());
        }
        return handler;
    }

//    /**
//     *
//     * @param event
//     */
//    @SuppressWarnings("unchecked")
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//        handlers.clear();
//        String[] requestHandlersNames = beanFactory.getBeanNamesForType(RequestHandler.class);
//        for (String beanName : requestHandlersNames) {
//            try {
//                BeanDefinition requestHandler = beanFactory.getBeanDefinition(beanName);
//                Class<?> handlerClass = Class.forName(requestHandler.getBeanClassName());
//                Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handlerClass, RequestHandler.class);
//                RequestHandler<Request, Object> handler = beanFactory.getBean(beanName, RequestHandler.class);
//                handlers.putIfAbsent(requestClass, handler);
//            } catch (ClassNotFoundException e) {
//                throw new NoHandlerForRequestException("request handler not found for class " + beanName, e);
//            }
//        }
//    }

    Map<Class<?>, RequestHandler<Request, Object>> getHandlers() {
        return Collections.unmodifiableMap(handlers);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        handlers.clear();
        List<String> requestHandlersNames = serviceNames(packagesToScan);
        for (String className : requestHandlersNames) {
            try {

                Class<?> clazz = Class.forName(className);
                //ClassBinding cb = bind(clazz).to(clazz);

                ServiceLocatorUtilities.bind(serviceLocator, new AbstractBinder() {

                    @Override
                    protected void configure() {
                        bind(clazz).to(clazz);
                    }
                });

                Object handler = serviceLocator.getService(clazz);

                if (handler != null) {

                }
                //handlers.putIfAbsent(clazz, handler);

//                Class<?> clazz = Class.forName(className);
//                ClassBinding cb = bind(clazz).to(clazz);



//                BeanDefinition requestHandler = beanFactory.getBeanDefinition(beanName);
//                Class<?> handlerClass = Class.forName(requestHandler.getBeanClassName());
//                Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handlerClass, RequestHandler.class);
//                RequestHandler<Request, Object> handler = beanFactory.getBean(beanName, RequestHandler.class);
//                handlers.putIfAbsent(requestClass, handler);
            } catch (ClassNotFoundException e) {
                throw new NoHandlerForRequestException("request handler not found for class " + className, e);
            }
        }
    }



    private static List<String> serviceNames(String... packages) {
        return new ClassGraph().whitelistPackages(packages)
            .scan()
            .getAllClasses()
            .getNames();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
