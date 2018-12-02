package jmediator.sample.jersey;

import jmediator.jersey.PackageScanningBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {
    public AppConfig() {
        packages("jmediator.sample.jersey");
        register(new PackageScanningBinder("jmediator.sample.jersey"));
    }
}
