package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

import com.seanthomascarroll.jmediator.DefaultServiceFactory;
import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.exporters.inmemory.InMemoryMetricExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.data.MetricData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenTelemetryMetricsBehaviorTest {

    private InMemoryMetricExporter exporter;
    private OpenTelemetryMetricsBehavior behavior;

    @BeforeEach
    void setUp() {
        exporter = InMemoryMetricExporter.create();
        behavior = new OpenTelemetryMetricsBehavior(OpenTelemetry.getMeterProvider());
    }

    // TODO: this probably should be two tests but at the moment I don't see an easy
    // way to clear metrics between tests from OpenTelemtry...sigh
    @Test
    void shouldTrackMetrics() {
        DefaultServiceFactory serviceFactory = new DefaultServiceFactory();
        serviceFactory.register(new PingHandler());
        serviceFactory.register(behavior);
        RequestDispatcher dispatcher = new RequestDispatcherImpl(serviceFactory);

        dispatcher.send(new Ping());
        dispatcher.send(new Ping());

        Collection<MetricData> metrics = OpenTelemetrySdk.getMeterProvider().getMetricProducer().collectAllMetrics();
        exporter.export(metrics);

        List<MetricData> countMetrics = exporter.getFinishedMetricItems()
            .stream()
            .filter(d -> "request.count".equals(d.getDescriptor().getName()))
            .collect(Collectors.toList());

        assertEquals(1, countMetrics.size());
        assertEquals(1, countMetrics.get(0).getPoints().size());
        MetricData.LongPoint countMetricDataPoint = (MetricData.LongPoint) countMetrics.get(0).getPoints().iterator().next();
        assertEquals(2, countMetricDataPoint.getValue());

        List<MetricData> timeMetrics = exporter.getFinishedMetricItems()
            .stream()
            .filter(d -> "request.time".equals(d.getDescriptor().getName()) && !d.getPoints().isEmpty())
            .collect(Collectors.toList());

        assertEquals(1, timeMetrics.size());
        assertEquals(1, timeMetrics.get(0).getPoints().size());
        MetricData.Point timeMetricDataPoint = timeMetrics.get(0).getPoints().iterator().next();
        assertEquals(1_000, (timeMetricDataPoint.getEpochNanos() - timeMetricDataPoint.getStartEpochNanos()) / 1e6, 200);
    }

}
