package jmediator.dropwizard;

import java.io.IOException;
import java.io.InputStream;

import org.glassfish.jersey.server.internal.scanning.AnnotationAcceptingListener;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

// TODO: similar to https://github.com/dropwizard/dropwizard/blob/c586517f5794cc761a9f9d26a46a6bb059f29597/dropwizard-hibernate/src/main/java/io/dropwizard/hibernate/ScanningHibernateBundle.java
abstract class ScanningJmediatorBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private final ImmutableList<Class<?>> entities;
    
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
    
    /**
     * Method scanning given directory for classes containing Hibernate @Entity annotation
     *
     * @param pckgs string array with packages containing Hibernate entities (classes annotated with @Entity annotation)
     *             e.g. com.codahale.fake.db.directory.entities
     * @return ImmutableList with classes from given directory annotated with Hibernate @Entity annotation
     */
    public static ImmutableList<Class<?>> findEntityClassesFromDirectory(String[] pckgs) {
        
        // TODO: need to find by interface not annotation
        @SuppressWarnings("unchecked")
        final AnnotationAcceptingListener asl = null; //new AnnotationAcceptingListener(Object.class);
        try (final PackageNamesScanner scanner = new PackageNamesScanner(pckgs, true)) {
            while (scanner.hasNext()) {
                final String next = scanner.next();
                if (asl.accept(next)) {
                    try (final InputStream in = scanner.open()) {
                        asl.process(next, in);
                    } catch (IOException e) {
                        throw new RuntimeException("AnnotationAcceptingListener failed to process scanned resource: " + next);
                    }
                }
            }
        }

        final Builder<Class<?>> builder = ImmutableList.builder();
        for (Class<?> clazz : asl.getAnnotatedClasses()) {
            builder.add(clazz);
        }

        return builder.build();
    }

}
