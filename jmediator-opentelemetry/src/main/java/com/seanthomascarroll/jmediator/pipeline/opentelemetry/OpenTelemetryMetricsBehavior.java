package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.MeterProvider;

import java.util.concurrent.TimeUnit;

public class OpenTelemetryMetricsBehavior implements PipelineBehavior {
    private final LongCounter counter;
    // TODO: de we want to keep a map of bound counters?
    private final LongHistogram latency;

    public OpenTelemetryMetricsBehavior(MeterProvider meterProvider) {
        // TODO: include library version
        Meter meter = meterProvider.get("jmediator");
        counter = meter.counterBuilder("request.count")
            .setDescription("RequestHandler count")
            .setUnit("long")
            .build();
        latency = meter.histogramBuilder("request.time")
            .ofLongs()
            .setDescription("RequestHandler Latency")
            .setUnit("ms")
            .build();
    }

    @Override
    public Object handle(Request request, PipelineChain chain) {
        // TODO: should we keep a map of bound metrics?
        Attributes attributes = Attributes.builder().put("request.name", request.getClass().getName()).build();
//        Labels labels = Labels.of("request.name", request.getClass().getName());
        counter.add(1, attributes);
        long start = System.nanoTime();
        try {
            return chain.doBehavior(request);
        } finally {
            latency.record(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start), attributes);
        }
    }
}
