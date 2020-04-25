package com.seanthomascarroll.jmediator.pipeline.micrometer;

import com.seanthomascarroll.jmediator.DefaultRequestHandlerProvider;
import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.RequestHandlerProvider;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MicrometerBehaviorTest {

    private MeterRegistry registry;
    private MicrometerBehavior behavior;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        behavior = new MicrometerBehavior(registry);

        RequestHandlerProvider requestHandlerProvider = new DefaultRequestHandlerProvider();
        requestHandlerProvider.register(new PingHandler());

        dispatcher = new RequestDispatcherImpl(requestHandlerProvider, Collections.singletonList(behavior));
    }

    @Test
    void shouldTrackCounts() {
        dispatcher.send(new Ping());
        dispatcher.send(new Ping());

        Counter counter = registry
            .get("request.count")
            .tag("request.name", Ping.class.getName())
            .counter();
        assertEquals(2, counter.count());
    }

    @Test
    void shouldTrackTime() {
        dispatcher.send(new Ping());

        Timer timer = registry
            .get("request.time")
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
