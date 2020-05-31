package com.seanthomascarroll.jmediator.sample.quarkus;

import com.seanthomascarroll.jmediator.pipeline.behaviors.LoggingPipelineBehavior;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

public class JmediatorBehaviorFactory {

    @Singleton
    @Produces
    public LoggingPipelineBehavior loggingBehavior() {
        return new LoggingPipelineBehavior();
    }

}
