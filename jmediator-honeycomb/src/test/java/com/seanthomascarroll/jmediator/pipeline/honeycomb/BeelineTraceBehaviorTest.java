package com.seanthomascarroll.jmediator.pipeline.honeycomb;

import com.seanthomascarroll.jmediator.DefaultServiceFactory;
import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import io.honeycomb.beeline.tracing.Beeline;
import io.honeycomb.beeline.tracing.Span;
import io.honeycomb.beeline.tracing.SpanBuilderFactory;
import io.honeycomb.beeline.tracing.SpanPostProcessor;
import io.honeycomb.beeline.tracing.Tracer;
import io.honeycomb.beeline.tracing.Tracing;
import io.honeycomb.beeline.tracing.sampling.TraceSampler;
import io.honeycomb.libhoney.HoneyClient;
import io.honeycomb.libhoney.LibHoney;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BeelineTraceBehaviorTest {

    private static final String WRITE_KEY = "test-write-key";
    private static final String DATASET   = "test-dataset";

    @Test
    void shouldTrace() {
        List<Object> capturedBeelineEvents = new ArrayList<>();
        TraceSampler<Object> captureSampler = input -> {
            if (input instanceof Span) {
                capturedBeelineEvents.add(input);
            }
            return 1;
        };

        HoneyClient client = LibHoney.create(LibHoney.options().setDataset(DATASET).setWriteKey(WRITE_KEY).build());
        Beeline beeline = createBeeline(client, captureSampler);

        DefaultServiceFactory serviceFactory = new DefaultServiceFactory();
        serviceFactory.register(new PingHandler());
        serviceFactory.register(new BeelineTraceBehavior(beeline));
        RequestDispatcher dispatcher = new RequestDispatcherImpl(serviceFactory);

        try (Span rootSpan = startTrace(beeline)) {
            beeline.getTracer().startTrace(rootSpan);
            dispatcher.send(new Ping());
        } finally {
            // close to flush events and release its thread pool
            client.close();
        }

        assertEquals(2, capturedBeelineEvents.size());
    }


    private static Span startTrace(Beeline beeline) {
        Span rootSpan = beeline.getSpanBuilderFactory().createBuilder()
            .setSpanName("test-root")
            .setServiceName("test-service")
            .build();

        return beeline.getTracer().startTrace(rootSpan);
    }

    private Beeline createBeeline(HoneyClient client, TraceSampler<Object> sampler) {
        SpanPostProcessor postProcessor = Tracing.createSpanProcessor(client, sampler);
        SpanBuilderFactory spanBuilderFactory = Tracing.createSpanBuilderFactory(postProcessor, sampler);
        Tracer tracer = Tracing.createTracer(spanBuilderFactory);
        return Tracing.createBeeline(tracer, spanBuilderFactory);
    }

}
