package com.seanthomascarroll.jmediator.pipeline.micrometer;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class MicrometerBehavior implements PipelineBehavior {
    private final MeterRegistry registry;

    /**
     *
     * @param registry
     */
    public MicrometerBehavior(MeterRegistry registry) {
        // TODO: allow callers to configure names metric names?
        this.registry = registry;
    }

    @Override
    public Object handle(Request request, PipelineChain chain) {
        List<Tag> tags = Collections.singletonList(Tag.of("request.name", request.getClass().getName()));

        registry.counter("request.count", tags).increment();

        return registry.timer("request.time", tags).record(() -> chain.doBehavior(request));
    }
}
