package jmediator.sample.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import jmediator.RequestDispatcher;
import jmediator.RequestDispatcherImpl;
import jmediator.dropwizard.JmediatorBundle;
import jmediator.dropwizard.RequestHandlerProviderImpl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.servlet.ServletProperties;

// https://github.com/dragonzone/dropwizard-hk2/blob/master/src/main/java/zone/dragon/dropwizard/HK2Bundle.java
// https://groups.google.com/forum/#!topic/dropwizard-user/Om_N_4WIZfQ
// https://www.dropwizard.io/1.0.0/docs/manual/internals.html
// https://github.com/dropwizard/dropwizard/issues/862
// https://github.com/dropwizard/dropwizard/issues/1026
// https://stackoverflow.com/questions/23224234/how-can-i-cleanly-override-the-default-servicelocator-used-by-jersey
// https://stackoverflow.com/questions/25719667/how-to-get-hk2-servicelocator-in-jersey-2-12
// https://stackoverflow.com/questions/21149161/jersey-and-hk2-servicelocator/21998307#21998307
// http://www.indestructiblevinyl.com/2016/07/25/roll-your-own-auto-discovery-in-jersey-and-hk2.html --> more applicable for jersey
public class SampleDropwizardApplication extends Application<SampleDropwizardConfiguration> {

    public static void main(String[] args) throws Exception {
        new SampleDropwizardApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello";
    }

//    private final HibernateBundle<SampleDropwizardConfiguration> hibernate = new HibernateBundle<SampleDropwizardConfiguration>(Person.class) {
//        @Override
//        public DataSourceFactory getDataSourceFactory(SampleDropwizardConfiguration configuration) {
//            return configuration.getDataSourceFactory();
//        }
//    };

    private final JmediatorBundle<SampleDropwizardConfiguration> jmediatorBundle = new JmediatorBundle<>();

    @Override
    public void initialize(Bootstrap<SampleDropwizardConfiguration> bootstrap) {
        // nothing to do yet
         bootstrap.addBundle(jmediatorBundle);
    }

    @Override
    public void run(SampleDropwizardConfiguration configuration, Environment environment) {

        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
//                ClassGraph scanner = new ClassGraph().whitelistPackages("jmediator.sample.dropwizard");
//                for (ClassInfo classInfo : scanner.scan().getClassesImplementing(RequestHandler.class.getName()).getStandardClasses()) {
//                    bind(classInfo.getClass()).to(classInfo.getClass());
//                }
                bind(HelloRequestHandler.class).to(HelloRequestHandler.class);
            }
        });

        //environment.jersey().register(new PackageScanningBinder("jmediator.sample.dropwizard"));

        // Looks like we cant do this here :(
        // ServiceLocator serviceLocator = ((ServletContainer) environment.getJerseyServletContainer()).getApplicationHandler().getServiceLocator();
        // doesnt work when in run
        // however this means I cant simply bind to jersey and use ServiceLocatorFactory within RequestHandlerProvider
        // these would be two different service locators and thus the one in RequestHandlerProvider would not find those
        // bound to jersey
        // Potentially could use a bridge or maybe parent service locator?
        // Would putting this all in a bundle help?
        // What about configuration?

//        final RequestHandlerProviderImpl provider = new RequestHandlerProviderImpl("jmediator.sample.dropwizard");
//        environment.servlets().addServletListeners(provider);
//        final RequestDispatcher dispatcher = new RequestDispatcherImpl(provider);
//        final HelloResource resource = new HelloResource(dispatcher);
//        environment.jersey().register(resource);
        environment.jersey().register(HelloResource.class);


//        environment.jersey().register(HelloResource.class);
//        environment.jersey().register(new AbstractBinder() {
//            @Override
//            protected void configure() {
//                bind(provider).to(RequestHandlerProvider.class);
//                bind(dispatcher).to(RequestDispatcher.class);
//            }
//        });
    }

}
