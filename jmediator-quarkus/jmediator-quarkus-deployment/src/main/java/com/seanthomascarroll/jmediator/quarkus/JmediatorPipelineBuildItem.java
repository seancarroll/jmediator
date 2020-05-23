package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import io.quarkus.builder.item.SimpleBuildItem;

import java.util.List;

public final class JmediatorPipelineBuildItem extends SimpleBuildItem {

    private final List<Class<PipelineBehavior>> behaviorClassNames;

    public JmediatorPipelineBuildItem(List<Class<PipelineBehavior>> behaviorClassNames) {
        this.behaviorClassNames = behaviorClassNames;
    }

    public List<Class<PipelineBehavior>> getBehaviorClassNames() {
        return behaviorClassNames;
    }
}
