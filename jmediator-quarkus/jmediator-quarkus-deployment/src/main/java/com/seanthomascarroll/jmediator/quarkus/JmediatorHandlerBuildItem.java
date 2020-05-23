package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.RequestHandler;
import io.quarkus.builder.item.MultiBuildItem;
import io.quarkus.builder.item.SimpleBuildItem;

import java.util.HashMap;
import java.util.Map;

public final class JmediatorHandlerBuildItem extends SimpleBuildItem { // extends MultiBuildItem {

    private Map<String, String> handlerClassNames = new HashMap<>();
    // private final Class<RequestHandler> requestHandlerClass;

    public JmediatorHandlerBuildItem(Map<String, String> handlerClassNames) {
        this.handlerClassNames = handlerClassNames;
    }

//    public Class<RequestHandler> getRequestHandlerClass() {
//        return requestHandlerClass;
//    }

    public Map<String, String> getHandlerClassNames() {
        return handlerClassNames;
    }
}
