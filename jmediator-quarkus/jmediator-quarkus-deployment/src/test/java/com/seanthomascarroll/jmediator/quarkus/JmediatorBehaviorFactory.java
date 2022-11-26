package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.pipeline.behaviors.LoggingPipelineBehavior;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
class JmediatorBehaviorFactory {

    @Produces
    public LoggingPipelineBehavior nullBehavior() {
        return new LoggingPipelineBehavior();
    }

}
