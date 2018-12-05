package jmediator.sample.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import jmediator.Request;
import jmediator.RequestDispatcher;
import jmediator.RequestDispatcherImpl;
import jmediator.RequestHandler;
import jmediator.dropwizard.PackageScanningBinder;
import jmediator.dropwizard.RequestHandlerProviderImpl;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;

import java.util.HashMap;
import java.util.Map;

public class SampleDropwizardApplication extends Application<SampleDropwizardConfiguration> {

    public static void main(String[] args) throws Exception {
        new SampleDropwizardApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello";
    }

    @Override
    public void initialize(Bootstrap<SampleDropwizardConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(SampleDropwizardConfiguration configuration,
                    Environment environment) {

//        environment.jersey().register(new AbstractBinder() {
//            @Override
//            protected void configure() {
//                ClassGraph scanner = new ClassGraph().whitelistPackages("jmediator.sample.dropwizard");
//                for (ClassInfo classInfo : scanner.scan().getClassesImplementing(RequestHandler.class.getName()).getStandardClasses()) {
//                    bind(classInfo.getClass()).to(classInfo.getClass());
//                }
//                // bind(HelloRequestHandler.class).to(HelloRequestHandler.class);
//            }
//        });

        //environment.jersey().register(new PackageScanningBinder());

        final RequestHandlerProviderImpl provider = new RequestHandlerProviderImpl();
        environment.servlets().addServletListeners(provider);
        final RequestDispatcher dispatcher = new RequestDispatcherImpl(provider);

        final HelloResource resource = new HelloResource(dispatcher);
        environment.jersey().register(resource);
    }

}
