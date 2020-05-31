package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import io.quarkus.builder.item.SimpleBuildItem;

import java.util.List;

public final class JmediatorPipelineBuildItem extends SimpleBuildItem {

    private final List<Class<? extends PipelineBehavior>> behaviorClassNames;

    public JmediatorPipelineBuildItem(List<Class<? extends PipelineBehavior>> behaviorClassNames) {
        this.behaviorClassNames = behaviorClassNames;
    }

    public List<Class<? extends PipelineBehavior>> getBehaviorClassNames() {
        return behaviorClassNames;
    }
}
