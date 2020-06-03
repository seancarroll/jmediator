package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.RequestHandler;
import io.quarkus.builder.item.SimpleBuildItem;

import java.util.Map;

public final class JmediatorHandlerBuildItem extends SimpleBuildItem {

    private final Map<String, Class<? extends RequestHandler<?, ?>>> handlerClassNames;

    public JmediatorHandlerBuildItem(Map<String, Class<? extends RequestHandler<?, ?>>> handlerClassNames) {
        this.handlerClassNames = handlerClassNames;
    }

    public Map<String, Class<? extends RequestHandler<?, ?>>> getHandlerClassNames() {
        return handlerClassNames;
    }
}
