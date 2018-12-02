package jmediator.jersey;

import jmediator.NoHandlerForRequestException;
import jmediator.Request;
import jmediator.RequestHandler;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.classgraph.ClassGraph;

import org.glassfish.hk2.api.ServiceLocator;

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
        for (String className : scanner.scan().getClassesImplementing(RequestHandler.class.getName()).getNames()) {
            try {
                Class<?> clazz = Class.forName(className);
                ClassBinding cb = bind(clazz).to(clazz);

                RequestHandler<Request, Object> handler = ServiceLocatorUtilities.getService(serviceLocator, className);
                // RequestHandler<Request, Object> handler = (RequestHandler)serviceLocator.getService(clazz);
                if (handler == null) {
                    throw new NoHandlerForRequestException("request handler not found for class " + className);
                }
                handlers.putIfAbsent(clazz, handler);
            } catch (Exception ex) {
                LOG.warn("Error binding class: {}", className, ex);
            }
        }
    }
}
