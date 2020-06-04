package com.seanthomascarroll.jmediator.pipeline.micrometer;

import com.seanthomascarroll.jmediator.DefaultServiceFactory;
import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import com.seanthomascarroll.jmediator.RequestHandler;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        DefaultServiceFactory serviceFactory = new DefaultServiceFactory();
        serviceFactory.register(new PingHandler());
        serviceFactory.register(behavior);

        dispatcher = new RequestDispatcherImpl(serviceFactory);
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
        assertEquals(500, timer.totalTime(TimeUnit.MILLISECONDS), 100);
    }


    private static class Ping implements Request {
    }

    private static class PingHandler implements RequestHandler<Ping, Pong> {

        @Override
        public Pong handle(Ping request) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // ignore
            }

            return new Pong("pong");
        }
    }

    private static class Pong {
        String message;

        Pong(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "Pong{" +
                "message='" + message + '\'' +
                '}';
        }
    }
}
