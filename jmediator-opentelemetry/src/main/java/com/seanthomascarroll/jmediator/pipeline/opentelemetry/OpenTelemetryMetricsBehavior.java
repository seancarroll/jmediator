package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.opentelemetry.metrics.LongCounter;
import io.opentelemetry.metrics.LongMeasure;
import io.opentelemetry.metrics.Meter;
import io.opentelemetry.metrics.MeterProvider;

public class OpenTelemetryMetricsBehavior implements PipelineBehavior {
    private final Meter meter;
    private final LongCounter counter;
    private final LongMeasure latency;

    public OpenTelemetryMetricsBehavior(MeterProvider meterProvider) {

        // TODO: include library version
        meter = meterProvider.get("jmediator");
        counter = meter.longCounterBuilder("request.count")
            .setDescription("RequestHandler count")
            .setUnit("long")
            .setMonotonic(true)
            .build();

        latency = meter.longMeasureBuilder("request.time")
            .setDescription("RequestHandler Latency")
            .setUnit("ms")
            .build();
    }

    @Override
    public <T extends Request> Object handle(T request, PipelineChain chain) {
        // TODO: should we keep a map of bound metrics?

        String[] labels = {"request.name", request.getClass().getName()};

        counter.add(1, labels);

        long start = System.currentTimeMillis();
        Object result = chain.doBehavior();
        latency.record(System.currentTimeMillis() - start, labels);

        return result;
    }
}
