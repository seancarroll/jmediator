package com.seanthomascarroll.jmediator.quarkus;

import io.quarkus.builder.item.SimpleBuildItem;

import java.util.List;

public final class JmediatorPipelineBuildItem extends SimpleBuildItem {

    private final List<String> behaviorClassNames;

    public JmediatorPipelineBuildItem(List<String> behaviorClassNames) {
        this.behaviorClassNames = behaviorClassNames;
    }

    public List<String> getBehaviorClassNames() {
        return behaviorClassNames;
    }
}
