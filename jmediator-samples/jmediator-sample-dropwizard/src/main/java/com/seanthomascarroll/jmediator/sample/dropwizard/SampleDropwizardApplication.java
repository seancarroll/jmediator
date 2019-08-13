package com.seanthomascarroll.jmediator.sample.dropwizard;

import com.seanthomascarroll.jmediator.jersey.JmediatorFeature;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.internal.inject.AbstractBinder;
//import org.glassfish.hk2.utilities.binding.AbstractBinder;

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

    @Override
    public void run(SampleDropwizardConfiguration configuration, Environment environment) {

        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(HelloRequestHandler.class).to(HelloRequestHandler.class);
            }
        });

        // environment.jersey().register(JmediatorFeature.class);
        environment.jersey().register(new JmediatorFeature("com.seanthomascarroll.jmediator.sample.dropwizard"));
        environment.jersey().register(HelloResource.class);
    }

}
