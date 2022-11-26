package com.seanthomascarroll.jmediator.sample.jersey;

import com.seanthomascarroll.jmediator.jersey.JmediatorFeature;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/*")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        packages("com.seanthomascarroll.jmediator.sample.jersey");

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(HelloRequestHandler.class).to(HelloRequestHandler.class);
            }
        });


        register(new JmediatorFeature("com.seanthomascarroll.jmediator.sample.jersey"));
    }
}
