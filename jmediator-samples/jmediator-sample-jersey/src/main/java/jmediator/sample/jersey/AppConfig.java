package jmediator.sample.jersey;

import jmediator.RequestDispatcher;
import jmediator.RequestDispatcherImpl;
import jmediator.RequestHandler;
import jmediator.RequestHandlerProvider;
import jmediator.jersey.JmediatorFeature;
import jmediator.jersey.PackageScanningBinder;
import jmediator.jersey.RequestHandlerProviderImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;


@ApplicationPath("/*")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        packages("jmediator.sample.jersey");


        register(new AbstractBinder() {
            @Override
            protected void configure() {
                //bind(new RequestHandlerProviderImpl("jmediator.sample.jersey")).to(RequestHandlerProvider.class);

//                RequestDispatcherImpl dispatcher = new RequestDispatcherImpl(new RequestHandlerProviderImpl("jmediator.sample.jersey"));
//                bind(dispatcher).to(RequestDispatcher.class);
                bind(HelloRequestHandler.class).to(HelloRequestHandler.class);
            }
        });
        //register(new PackageScanningBinder("jmediator.sample.jersey"));

        register(new JmediatorFeature("jmediator.sample.jersey"));
    }
}
