package com.seanthomascarroll.jmediator.pipeline.honeycomb;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;

import java.util.function.Supplier;

/**
 * Pipeline behavior that captures handler call times and adds to a Honeycomb client Event
 */
public class HoneycombMetricsBehavior implements PipelineBehavior {

    private final HoneycombContext honeycombContext;

    public HoneycombMetricsBehavior(HoneycombContext honeycombContext) {
        this.honeycombContext = honeycombContext;
    }

    @Override
    public Object handle(Request request, PipelineChain chain) {
        return timeCall(() -> chain.doBehavior(request), "timers." + request.getClass().getName());
    }

    private <T> T timeCall(final Supplier<T> call, final String callName) {
        final long startTime = System.currentTimeMillis();
        try {
            return call.get();
        } finally {
            final long endTime = System.currentTimeMillis();
            honeycombContext.getEvent().addField(callName, endTime - startTime);
        }
    }

}
