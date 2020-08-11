package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.opentelemetry.common.Labels;
import io.opentelemetry.metrics.LongCounter;
import io.opentelemetry.metrics.LongValueRecorder;
import io.opentelemetry.metrics.Meter;
import io.opentelemetry.metrics.MeterProvider;

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
    public <T extends Request> Object handle(T request, PipelineChain<T> chain) {
        // TODO: should we keep a map of bound metrics?

        Labels labels = Labels.of("request.name", request.getClass().getName());

        counter.add(1, labels);

        long start = System.currentTimeMillis();
        Object result = chain.doBehavior(request);
        latency.record(System.currentTimeMillis() - start, labels);

        return result;
    }
}
