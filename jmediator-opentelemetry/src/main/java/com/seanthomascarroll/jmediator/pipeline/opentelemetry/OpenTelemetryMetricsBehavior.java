package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.opentelemetry.api.common.Labels;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongValueRecorder;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.MeterProvider;

public class OpenTelemetryMetricsBehavior implements PipelineBehavior {
    private final Meter meter;
    private final LongCounter counter;

    // TODO: de we want to keep a map of bound counters?
    private final LongValueRecorder latency;

    public OpenTelemetryMetricsBehavior(MeterProvider meterProvider) {

        // TODO: include library version
        meter = meterProvider.get("jmediator");
        counter = meter.longCounterBuilder("request.count")
            .setDescription("RequestHandler count")
            .setUnit("long")
            .build();
        latency = meter.longValueRecorderBuilder("request.time")
            .setDescription("RequestHandler Latency")
            .setUnit("ms")
            .build();
    }

    @Override
    public Object handle(Request request, PipelineChain chain) {
        // TODO: should we keep a map of bound metrics?

        Labels labels = Labels.of("request.name", request.getClass().getName());

        counter.add(1, labels);

        long start = System.currentTimeMillis();
        Object result = chain.doBehavior(request);
        latency.record(System.currentTimeMillis() - start, labels);

        return result;
    }
}
