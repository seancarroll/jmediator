package com.seanthomascarroll.jmediator.sample.quarkus;

import com.seanthomascarroll.jmediator.pipeline.behaviors.LoggingPipelineBehavior;
import io.quarkus.arc.Unremovable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

// @ApplicationScoped
public class JmediatorBehaviorFactory {

    // TODO: At the moment Quarkus will remove PipelineBehavior beans as they are not explicitly referenced anywhere.
    // For now @Unremovable is a work around however I need to update the Jmediator extension to properly identify
    // unremovable beans
    @Unremovable
    @Singleton
    public LoggingPipelineBehavior loggingBehavior() {
        return new LoggingPipelineBehavior();
    }

}
