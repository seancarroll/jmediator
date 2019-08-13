package com.seanthomascarroll.jmediator.opencensus.pipeline;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.opencensus.common.Scope;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;

// https://github.com/census-instrumentation/opencensus-java
// https://opencensus.io/quickstart/java/
// https://opencensus.io/quickstart/java/tracing/
// https://opencensus.io/quickstart/java/metrics/
/**
 *
 */
public class OpenCensusBehavior implements PipelineBehavior {

    private static final Tracer tracer = Tracing.getTracer();



    @Override
    public <T extends Request> Object handle(T request, PipelineChain chain) {
        // Create a child Span of the current Span. Always record events for this span and force it to
        // be sampled. This makes it easier to try out the example, but unless you have a clear use
        // case, you don't need to explicitly set record events or sampler.
        try (Scope ss = tracer
                     .spanBuilder("MyChildWorkSpan")
                     .setRecordEvents(true)
                     .setSampler(Samplers.alwaysSample())
                     .startScopedSpan()) {
            doInitialWork();
            tracer.getCurrentSpan().addAnnotation("Finished initial work");
            doFinalWork();
            return chain.doBehavior();
        }
    }

    private static void doInitialWork() {
        // ...
        tracer.getCurrentSpan().addAnnotation("Important.");
        // ...
    }

    private static void doFinalWork() {
        // ...
        tracer.getCurrentSpan().addAnnotation("More important.");
        // ...
    }
}
