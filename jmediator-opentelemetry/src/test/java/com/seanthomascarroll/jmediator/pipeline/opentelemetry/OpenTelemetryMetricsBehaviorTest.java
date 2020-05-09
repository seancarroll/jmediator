package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

import com.seanthomascarroll.jmediator.DefaultServiceFactory;
import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.metrics.Meter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.MeterSdkProvider;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.IntervalMetricReader;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//import com.newrelic.telemetry.Attributes;
//import com.newrelic.telemetry.SimpleMetricBatchSender;
//import com.newrelic.telemetry.TelemetryClient;
//import com.newrelic.telemetry.metrics.Metric;
//import com.newrelic.telemetry.metrics.MetricBatchSenderBuilder;
//import com.newrelic.telemetry.metrics.MetricBuffer;
import io.opentelemetry.sdk.internal.MillisClock;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.MetricData.Descriptor;
import io.opentelemetry.sdk.metrics.data.MetricData.Descriptor.Type;
import io.opentelemetry.sdk.metrics.data.MetricData.Point;
import io.opentelemetry.sdk.metrics.export.MetricExporter;


class OpenTelemetryMetricsBehaviorTest {

    OpenTelemetryMetricsBehavior behavior;
    MeterSdkProvider meterProvider;

//    private static class SimpleMetricExporter implements MetricExporter {
//
//
//        @Override
//        public ResultCode export(Collection<MetricData> metrics) {
//            return ResultCode.SUCCESS;
//        }
//
//        @Override
//        public ResultCode flush() {
//            return ResultCode.SUCCESS;
//        }
//
//        @Override
//        public void shutdown() {
//
//        }
//    }


//        String apiKey = System.getenv("INSIGHTS_INSERT_KEY");
//
//        MetricBatchSender metricBatchSender =
//            SimpleMetricBatchSender.builder(apiKey).enableAuditLogging().build();
//        TelemetryClient telemetryClient = new TelemetryClient(metricBatchSender, spanBatchSender);
//        Attributes serviceAttributes = new Attributes().put("service.name", "best service ever");
//        MetricExporter metricExporter =
//            NewRelicMetricExporter.newBuilder()
//                .telemetryClient(telemetryClient)
//                .commonAttributes(serviceAttributes)
//                .build();
//
//        IntervalMetricReader intervalMetricReader =
//            IntervalMetricReader.builder()
//                .setMetricProducers(Collections.singleton(OpenTelemetrySdk.getMeterProvider().getMetricProducer()))
//                .setExportIntervalMillis(5000)
//                .setMetricExporter(metricExporter)
//                .build();



    @BeforeEach
    void setUp() {
        // This sucks...opentelemetry should expose a registry rather having to do this junk
        meterProvider = (MeterSdkProvider) OpenTelemetry.getMeterProvider();
        behavior = new OpenTelemetryMetricsBehavior(meterProvider);
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

        // verify counts
        Meter countMeter = OpenTelemetry.getMeterProvider().get("request.count");

        assertNotNull(countMeter);

        // ugh...this seems like it should be easier
        List<MetricData> countMetrics = meterProvider.getMetricProducer().getAllMetrics()
            .stream()
            .filter(d -> "request.count".equals(d.getDescriptor().getName()))
            .collect(Collectors.toList());

        assertEquals(1, countMetrics.size());
        assertEquals(1, countMetrics.get(0).getPoints().size());
        MetricData.LongPoint countMetricDataPoint = (MetricData.LongPoint) countMetrics.get(0).getPoints().iterator().next();
        assertEquals(2, countMetricDataPoint.getValue());

        // verify time
        Meter meter = OpenTelemetry.getMeterProvider().get("request.time");
        assertNotNull(meter);

        // ugh...this seems like it should be easier
        List<MetricData> timeMetrics = meterProvider.getMetricProducer().getAllMetrics()
            .stream()
            .filter(d -> "request.time".equals(d.getDescriptor().getName()))
            .collect(Collectors.toList());

        assertEquals(1, timeMetrics.size());
        assertEquals(1, timeMetrics.get(0).getPoints().size());
        MetricData.Point timeMetricDataPoint = timeMetrics.get(0).getPoints().iterator().next();
        assertEquals(1_000, (timeMetricDataPoint.getEpochNanos() - timeMetricDataPoint.getStartEpochNanos()) / 1e6, 200);
    }

}
