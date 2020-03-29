package com.seanthomascarroll.jmediator.pipeline.micrometer;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import com.seanthomascarroll.jmediator.pipeline.PipelineChainImpl;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.search.RequiredSearch;
import io.micrometer.core.instrument.search.Search;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MicrometerBehaviorTest {

    private MeterRegistry registry;
    private MicrometerBehavior behavior;
    private PipelineChain pipelineChain;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        behavior = new MicrometerBehavior(registry);
        pipelineChain = mock(PipelineChain.class);
    }


    @Test
    void shouldTrackCounts() {
        when(pipelineChain.doBehavior()).thenReturn(null);

        behavior.handle(new Ping(), pipelineChain);
        behavior.handle(new Ping(), pipelineChain);

        Counter counter = registry
            .get("app.request.count")
            .tag("request.name", Ping.class.getName())
            .counter();
        assertEquals(2, counter.count());

    }

    @Test
    void shouldTrackTime() {
        PipelineChain pipelineChain = new PipelineChainImpl(new Ping(), Collections.singletonList(behavior), new PingHandler());
        pipelineChain.doBehavior();

        Timer timer = registry
            .get("app.request.time")
            .tag("request.name", Ping.class.getName())
            .timer();
        assertEquals(500, timer.totalTime(TimeUnit.MILLISECONDS), 10);
    }


    private static class Ping implements Request {
        Ping() {

        }

        Ping(String message) {
            this.message = message;
        }

        String message;

        @Override
        public String toString() {
            return "Ping{" +
                "message='" + message + '\'' +
                '}';
        }
    }

    private static class PingHandler implements RequestHandler<Ping, Pong> {

        @Override
        public Pong handle(Ping request) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // ignore
            }

            return new Pong("hello " + request.message);
        }
    }

    private static class Pong {
        Pong(String message) {
            this.message = message;
        }

        String message;

        @Override
        public String toString() {
            return "Pong{" +
                "message='" + message + '\'' +
                '}';
        }
    }
}
