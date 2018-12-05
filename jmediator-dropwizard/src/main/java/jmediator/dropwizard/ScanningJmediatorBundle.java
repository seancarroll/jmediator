package jmediator.dropwizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jmediator.ReflectionUtils;
import jmediator.Request;
import jmediator.RequestHandler;
import org.glassfish.hk2.api.*;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import com.google.common.collect.ImmutableList;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;

// https://github.com/Netflix/governator/blob/master/governator-jersey/src/main/java/com/netflix/governator/guice/jersey/GovernatorComponentProviderFactory.java
// https://stackoverflow.com/questions/45620576/jersey-2-26-and-spring-4-3-10-but-no-hk2#comment78209510_45624308
// https://stackoverflow.com/questions/46825067/how-to-get-hk2-servicelocator-in-a-jersey2-servletcontainer
// https://javaee.github.io/hk2/guice-bridge.html
// https://github.com/darmbrust/HK2Utilities
// ServiceLocator locator = ServiceLocatorFactory.getInstance().create(serviceLocatorName, parentServiceLocator);

// http://www.indestructiblevinyl.com/2016/07/25/roll-your-own-auto-discovery-in-jersey-and-hk2.html
// https://stackoverflow.com/questions/23402814/how-does-servicelocator-find-service-and-contact-automatically-in-hk2
// https://javaee.github.io/hk2/introduction.html#named-services
// https://javaee.github.io/hk2/
// https://javaee.github.io/hk2/aop-example.html
// http://www.justinleegrant.com/?p=516
// look at dropwizard more closely
// TODO: similar to https://github.com/dropwizard/dropwizard/blob/c586517f5794cc761a9f9d26a46a6bb059f29597/dropwizard-hibernate/src/main/java/io/dropwizard/hibernate/ScanningHibernateBundle.java
// how to get all services registered through Binder aka bind() method?
// similar to how we handle Spring, I think we only want to scan/find classes that have been registered
// we could look at an autoscan feature that could be used but I think that likely already exists
// and ultimately we just need to be concerned with how to grab them
abstract class ScanningJmediatorBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private final ImmutableList<Class<?>> entities;

    private Map<Class<?>, RequestHandler<Request, Object>> handlers = new HashMap<>();

    // TODO: pass in as dependency. This could be the default
    private ServiceLocator serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator();

    // rather than ServiceLocator the recommended approach is to use the injectionManager
    // private InjectionManager injectionManager = InjectionManager.
    // InjectionManagerProvider p =

    /**
     * @param pckg string with package containing request handlers entities (classes implementing RequestHandlerannotated with Hibernate {@code @Entity}
     *             annotation) e. g. {@code com.codahale.fake.db.directory.entities}
     */
    protected ScanningJmediatorBundle(String pckg) {
        this(new String[]{pckg});
    }
    
    protected ScanningJmediatorBundle(String[] pckgs) {
        entities = findEntityClassesFromDirectory(pckgs);
    }
    
    public void initialize(Bootstrap<?> bootstrap) {
        // TODO Auto-generated method stub
    }

    public void run(T configuration, Environment environment) throws Exception {
        // TODO Auto-generated method stub

    }

    private void registerHandlers() {

        List<RequestHandler> requestHandlers = serviceLocator.getAllServices(RequestHandler.class);
        for (RequestHandler handler : requestHandlers) {
            Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handler.getClass(), RequestHandler.class);
            handlers.putIfAbsent(requestClass, handler);
        }
    }
    
    /**
     * Method scanning given directory for classes containing Hibernate @Entity annotation
     *
     * @param pckgs string array with packages containing Hibernate entities (classes annotated with @Entity annotation)
     *             e.g. com.codahale.fake.db.directory.entities
     * @return ImmutableList with classes from given directory annotated with Hibernate @Entity annotation
     */
    public static ImmutableList<Class<?>> findEntityClassesFromDirectory(String[] pckgs) {


        List<String> classesFound = new ArrayList<>();

        // TODO: need to find by interface not annotation
//        @SuppressWarnings("unchecked")
//        final AnnotationAcceptingListener asl = null; //new AnnotationAcceptingListener(Object.class);
//        try (final PackageNamesScanner scanner = new PackageNamesScanner(pckgs, true)) {
//            while (scanner.hasNext()) {
//                final String next = scanner.next();
//                classesFound.add(next);
////                if (asl.accept(next)) {
////                    try (final InputStream in = scanner.open()) {
////                        asl.process(next, in);
////                    } catch (IOException e) {
////                        throw new RuntimeException("ScanningJmediatorBundle failed to process scanned resource: " + next);
////                    }
////                }
//            }
//        }

//        final Builder<Class<?>> builder = ImmutableList.builder();
//        for (Class<?> clazz : asl.getAnnotatedClasses()) {
//            builder.add(clazz);
//        }

//        return builder.build();

        return null;
    }

}
