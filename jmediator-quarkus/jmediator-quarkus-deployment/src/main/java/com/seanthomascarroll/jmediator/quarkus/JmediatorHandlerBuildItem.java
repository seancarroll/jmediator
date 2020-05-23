package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.RequestHandler;
import io.quarkus.builder.item.SimpleBuildItem;

import java.util.Map;

public final class JmediatorHandlerBuildItem extends SimpleBuildItem {

    private final Map<String, Class<RequestHandler>> handlerClassNames;

    public JmediatorHandlerBuildItem(Map<String, Class<RequestHandler>> handlerClassNames) {
        this.handlerClassNames = handlerClassNames;
    }

    public Map<String, Class<RequestHandler>> getHandlerClassNames() {
        return handlerClassNames;
    }
}
