package jmediator.jersey;

import jmediator.ReflectionUtils;
import jmediator.Request;
import jmediator.RequestHandler;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.classgraph.ClassGraph;

import org.glassfish.hk2.api.ServiceLocator;

import java.util.HashMap;
import java.util.Map;

public class PackageScanningBinder extends AbstractBinder {

    private static final Logger LOG = LoggerFactory.getLogger(PackageScanningBinder.class);

    private ServiceLocator serviceLocator;

    private Map<Class<?>, RequestHandler<Request, Object>> handlers = new HashMap<>();
    private final ClassGraph scanner;

    public PackageScanningBinder(String... packages) {
        this.scanner = new ClassGraph().whitelistPackages(packages);
        serviceLocator = ServiceLocatorFactory.getInstance().create("uniqueName");
    }


    @Override
    protected void configure() {
        for (String className : scanner.scan().getClassesImplementing(RequestHandler.class.getName()).getNames()) {
            try {
                Class<?> clazz = Class.forName(className);
                ClassBinding cb = bind(clazz).to(clazz);

                RequestHandler<Request, Object> handler = (RequestHandler)serviceLocator.getService(clazz);
                handlers.putIfAbsent(clazz, handler);

                // BeanInfo
                // BeanUtilities

                //Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(clazz, RequestHandler.class);
                //RequestHandler<Request, Object> handler = beanFactory.getBean(beanName, RequestHandler.class);
                //handlers.putIfAbsent(requestClass, handler);

                // dropwizard
                // ServiceLocator serviceLocator = ((ServletContainer) environment.getJerseyServletContainer()).getApplicationHandler().getServiceLocator();


                //ClassBinding cb = bind(clazz).to(clazz);
                //class<?> handlerClass = Class.forName(requestHandler.getBeanClassName());
                //BeanDefinition requestHandler = beanFactory.getBeanDefinition(beanName);
                //Class<?> handlerClass = Class.forName(requestHandler.getBeanClassName());
                //Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handlerClass, RequestHandler.class);
                //RequestHandler<Request, Object> handler = beanFactory.getBean(beanName, RequestHandler.class);
                //handlers.putIfAbsent(requestClass, handler);
            } catch (Exception ex) {
                LOG.warn("Error binding class: {}", className, ex);
            }
        }
    }
}
