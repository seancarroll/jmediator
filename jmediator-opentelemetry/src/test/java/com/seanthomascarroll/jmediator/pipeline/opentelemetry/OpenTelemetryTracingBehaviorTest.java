package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

import com.seanthomascarroll.jmediator.DefaultServiceFactory;
import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import com.seanthomascarroll.jmediator.RequestHandler;
import io.opentelemetry.exporters.inmemory.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.TracerSdkProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.trace.Status;
import io.opentelemetry.trace.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenTelemetryTracingBehaviorTest {

    private final TracerSdkProvider tracerSdkProvider = TracerSdkProvider.builder().build();
    private final Tracer tracer = tracerSdkProvider.get("test");
    private final InMemorySpanExporter exporter = InMemorySpanExporter.create();
    private OpenTelemetryTracingBehavior behavior;

    @BeforeEach
    public void setUp() {
        tracerSdkProvider.addSpanProcessor(SimpleSpanProcessor.newBuilder(exporter).build());
        behavior = new OpenTelemetryTracingBehavior(tracer);
    }

    @Test
    void shouldAddSpan() {
        DefaultServiceFactory serviceFactory = new DefaultServiceFactory();
        serviceFactory.register(new PingHandler());
        serviceFactory.register(behavior);
        RequestDispatcher dispatcher = new RequestDispatcherImpl(serviceFactory);

        dispatcher.send(new Ping());

        List<SpanData> spanItems = exporter.getFinishedSpanItems();
        assertNotNull(spanItems);
        assertEquals(1, spanItems.size());
        assertEquals("Ping", spanItems.get(0).getName());
    }

    @Test
    void shouldAddErrorWhenExceptionOccurs() {
        DefaultServiceFactory serviceFactory = new DefaultServiceFactory();
        serviceFactory.register(new FaultyPingHandler());
        serviceFactory.register(behavior);
        RequestDispatcher dispatcher = new RequestDispatcherImpl(serviceFactory);

        assertThrows(RuntimeException.class, () -> dispatcher.send(new Ping()));

        List<SpanData> spanItems = exporter.getFinishedSpanItems();
        assertNotNull(spanItems);
        assertEquals(1, spanItems.size());
        assertEquals("Ping", spanItems.get(0).getName());
        assertEquals(Status.CanonicalCode.UNKNOWN, spanItems.get(0).getStatus().getCanonicalCode());
        assertEquals("an error occurred", spanItems.get(0).getStatus().getDescription());

        List<SpanData.Event> events = spanItems.get(0).getEvents();
        assertNotNull(events);
        assertEquals(1, events.size());
        SpanData.Event error = events.get(0);
        assertEquals("error", error.getName());
        assertEquals(2, error.getAttributes().size());
        assertEquals("RuntimeException", error.getAttributes().get("error.type").getStringValue());
        assertEquals("an error occurred", error.getAttributes().get("error.message").getStringValue());
    }

    private static class FaultyPingHandler implements RequestHandler<Ping, Pong> {

        @Override
        public Pong handle(Ping request) {
            throw new RuntimeException("an error occurred");
        }
    }
}
