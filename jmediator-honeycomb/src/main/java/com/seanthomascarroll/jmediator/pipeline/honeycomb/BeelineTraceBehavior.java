package com.seanthomascarroll.jmediator.pipeline.honeycomb;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.honeycomb.beeline.tracing.Beeline;
import io.honeycomb.beeline.tracing.Span;
import io.honeycomb.libhoney.HoneyClient;
import io.honeycomb.libhoney.LibHoney;

public class BeelineTraceBehavior implements PipelineBehavior {

    private final Beeline beeline;

    // https://github.com/honeycombio/examples/blob/master/java-webapp/src/main/java/io/honeycomb/libhoney/example/webapp/instrumentation/HoneycombContext.java
    // private final HoneycombContext honeycombContext;

    public BeelineTraceBehavior(Beeline beeline) {
        this.beeline = beeline;
    }

    @Override
    public <T extends Request> Object handle(T request, PipelineChain chain) {
        // beeline.getActiveSpan().addField("request", request.getClass().getName());
        try (Span childSpan = beeline.startChildSpan(request.getClass().getName())) {
            // childSpan.addField("request", request.getClass().getName());
            return chain.doBehavior();
        }
    }

}
