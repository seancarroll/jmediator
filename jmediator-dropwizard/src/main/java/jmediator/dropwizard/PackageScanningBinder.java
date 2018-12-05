package jmediator.dropwizard;

import io.github.classgraph.ClassGraph;
import jmediator.Request;
import jmediator.RequestHandler;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

// TODO: do we need this?
public class PackageScanningBinder extends AbstractBinder {

    private static final Logger LOG = LoggerFactory.getLogger(PackageScanningBinder.class);

    private ServiceLocator serviceLocator;
    private final ClassGraph scanner;
    private Map<Class<?>, RequestHandler<Request, Object>> handlers = new HashMap<>();

    public PackageScanningBinder(String... packages) {
        this.scanner = new ClassGraph().whitelistPackages(packages);
        this.serviceLocator = ServiceLocatorFactory.getInstance().create("PackageScanningBinder");
    }


    @Override
    protected void configure() {

//        for (ClassInfo classInfo : scanner.scan().getClassesImplementing(RequestHandler.class.getName()).getStandardClasses()) {
//            try {
//                Class<?> clazz = Class.forName(classInfo.getName());
//                // TODO: grab scope annotation
//                // I'm sure someone else must have a working classpath scanning binder
//                ClassBinding cb = bind(clazz).to(clazz);
//            } catch (Exception ex) {
//                LOG.warn("Error binding class: {}", classInfo.getName(), ex);
//            }
//        }


        for (String className : scanner.scan().getClassesImplementing(RequestHandler.class.getName()).getNames()) {
            try {
                Class<?> clazz = Class.forName(className);
                ClassBinding cb = bind(clazz).to(clazz);

//                RequestHandler<Request, Object> handler = ServiceLocatorUtilities.getService(serviceLocator, className);
//                // RequestHandler<Request, Object> handler = (RequestHandler)serviceLocator.getService(clazz);
//                if (handler == null) {
//                    throw new NoHandlerForRequestException("request handler not found for class " + className);
//                }
//                handlers.putIfAbsent(clazz, handler);
            } catch (Exception ex) {
                LOG.warn("Error binding class: {}", className, ex);
            }
        }
    }
}
