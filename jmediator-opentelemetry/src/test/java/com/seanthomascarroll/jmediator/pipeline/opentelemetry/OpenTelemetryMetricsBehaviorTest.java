package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

import com.seanthomascarroll.jmediator.DefaultServiceFactory;
import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import io.opentelemetry.exporters.inmemory.InMemoryMetricExporter;
import io.opentelemetry.sdk.testing.exporter.InMemoryMetricReader;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.data.SummaryPointData;
import io.opentelemetry.sdk.metrics.data.LongPointData;
import io.opentelemetry.sdk.metrics.data.MetricData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenTelemetryMetricsBehaviorTest {

    private InMemoryMetricExporter exporter;
    private OpenTelemetryMetricsBehavior behavior;
    private SdkMeterProvider sdkMeterProvider;
    private InMemoryMetricReader sdkMeterReader;

//    private final SdkMeterProviderBuilder sdkMeterProviderBuilder =
//        SdkMeterProvider.builder().setClock(testClock).setResource(RESOURCE);

    @BeforeEach
    void setUp() {
        exporter = InMemoryMetricExporter.create();
        sdkMeterReader = InMemoryMetricReader.create();
        sdkMeterProvider =
            SdkMeterProvider.builder().registerMetricReader(sdkMeterReader).build();


//        behavior = new OpenTelemetryMetricsBehavior(sdkMeterProvider);
//        InMemoryMetricReader sdkMeterReader = InMemoryMetricReader.create();
//        SdkMeterProvider sdkMeterProvider =
//            sdkMeterProviderBuilder.registerMetricReader(sdkMeterReader).build();
    }

    // TODO: this probably should be two tests but at the moment I don't see an easy
    // way to clear metrics between tests from OpenTelemetry...sigh
    @Test
    void shouldTrackMetrics() {
        DefaultServiceFactory serviceFactory = new DefaultServiceFactory();
        serviceFactory.register(new PingHandler());
        serviceFactory.register(behavior);
        RequestDispatcher dispatcher = new RequestDispatcherImpl(serviceFactory);

        dispatcher.send(new Ping());
        dispatcher.send(new Ping());


//        exporter.export(sdkMeterProvider.collectAllMetrics());
        sdkMeterReader.collectAllMetrics();

        List<MetricData> countMetrics = exporter.getFinishedMetricItems()
            .stream()
            .filter(d -> "request.count".equals(d.getName()))
            .collect(Collectors.toList());

        assertEquals(1, countMetrics.size());
        assertEquals(1, countMetrics.get(0).getLongSumData().getPoints().size());
        LongPointData countMetricDataPoint = countMetrics.get(0).getLongSumData().getPoints().iterator().next();
        assertEquals(2, countMetricDataPoint.getValue());

        List<MetricData> timeMetrics = exporter.getFinishedMetricItems()
            .stream()
            .filter(d -> "request.time".equals(d.getName()))
            .collect(Collectors.toList());

        assertEquals(1, timeMetrics.size());
        assertEquals(1, timeMetrics.get(0).getSummaryData().getPoints().size());
        SummaryPointData timeMetricDataPoint = timeMetrics.get(0).getSummaryData().getPoints().iterator().next();
        assertEquals(1_000, (timeMetricDataPoint.getEpochNanos() - timeMetricDataPoint.getStartEpochNanos()) / 1e6, 300);
    }

}
