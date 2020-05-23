package com.seanthomascarroll.jmediator.quarkus;

import io.quarkus.builder.item.MultiBuildItem;
import io.quarkus.builder.item.SimpleBuildItem;

import java.util.List;

public final class JmediatorPipelineBuildItem extends SimpleBuildItem {

    // private final Class<PipelineBehavior> behaviorClass;
    private final List<String> behaviorClassNames;

//    public JmediatorPipelineBuildItem(Class<PipelineBehavior> behaviorClass) {
//        this.behaviorClass = behaviorClass;
//    }

    public JmediatorPipelineBuildItem(List<String> behaviorClassNames) {
        this.behaviorClassNames = behaviorClassNames;
    }


//    public Class<PipelineBehavior> getBehaviorClass() {
//        return behaviorClass;
//    }


    public List<String> getBehaviorClassNames() {
        return behaviorClassNames;
    }
}
