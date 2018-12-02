package jmediator.sample.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import jmediator.RequestDispatcher;
import jmediator.RequestDispatcherImpl;
import jmediator.dropwizard.RequestHandlerProviderImpl;

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

        final RequestHandlerProviderImpl provider = new RequestHandlerProviderImpl();
        final RequestDispatcher dispatcher = new RequestDispatcherImpl(provider);

        final HelloResource resource = new HelloResource(dispatcher);
        environment.jersey().register(resource);
    }

}
