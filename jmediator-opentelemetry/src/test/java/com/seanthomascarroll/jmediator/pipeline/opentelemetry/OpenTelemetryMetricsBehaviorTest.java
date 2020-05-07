package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

import com.seanthomascarroll.jmediator.DefaultServiceFactory;
import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.metrics.Meter;
import io.opentelemetry.sdk.metrics.MeterSdkProvider;
import io.opentelemetry.sdk.metrics.data.MetricData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenTelemetryMetricsBehaviorTest {

    OpenTelemetryMetricsBehavior behavior;
    MeterSdkProvider meterProvider;

    @BeforeEach
    void setUp() {
        // This sucks...opentelemetry should expose a registry rather having to do this junk
        meterProvider = (MeterSdkProvider) OpenTelemetry.getMeterProvider();
        behavior = new OpenTelemetryMetricsBehavior(meterProvider);
    }

    @Test
    void shouldTrackCounts() {
        DefaultServiceFactory serviceFactory = new DefaultServiceFactory();
        serviceFactory.register(new PingHandler());
        serviceFactory.register(behavior);
        RequestDispatcher dispatcher = new RequestDispatcherImpl(serviceFactory);

        dispatcher.send(new Ping());
        dispatcher.send(new Ping());

        Meter meter = OpenTelemetry.getMeterProvider().get("request.count");

        assertNotNull(meter);

        // ugh...this seems like it should be easier
        List<MetricData> metrics = meterProvider.getMetricProducer().getAllMetrics()
            .stream()
            .filter(d -> "request.count".equals(d.getDescriptor().getName()))
            .collect(Collectors.toList());

        assertEquals(1, metrics.size());
        assertEquals(1, metrics.get(0).getPoints().size());
        MetricData.LongPoint point = (MetricData.LongPoint) metrics.get(0).getPoints().iterator().next();
        assertEquals(2, point.getValue());
    }

    @Test
    void shouldTrackTime() {
        DefaultServiceFactory serviceFactory = new DefaultServiceFactory();
        serviceFactory.register(new PingHandler());
        serviceFactory.register(behavior);
        RequestDispatcher dispatcher = new RequestDispatcherImpl(serviceFactory);

        dispatcher.send(new Ping());

        Meter meter = OpenTelemetry.getMeterProvider().get("request.time");

        assertNotNull(meter);

        // ugh...this seems like it should be easier
        List<MetricData> metrics = meterProvider.getMetricProducer().getAllMetrics()
            .stream()
            .filter(d -> "request.time".equals(d.getDescriptor().getName()))
            .collect(Collectors.toList());

        assertEquals(1, metrics.size());
        assertEquals(1, metrics.get(0).getPoints().size());
        MetricData.Point point = metrics.get(0).getPoints().iterator().next();
        assertEquals(500, (point.getEpochNanos() - point.getStartEpochNanos()) / 1e6, 50);
    }

}
