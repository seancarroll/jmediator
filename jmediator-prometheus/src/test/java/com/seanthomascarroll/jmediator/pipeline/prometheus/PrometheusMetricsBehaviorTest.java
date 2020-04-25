package com.seanthomascarroll.jmediator.pipeline.prometheus;

import com.seanthomascarroll.jmediator.DefaultRequestHandlerProvider;
import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.RequestHandlerProvider;
import io.prometheus.client.CollectorRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrometheusMetricsBehaviorTest {

    private CollectorRegistry registry;
    private PrometheusMetricsBehavior behavior;
    private RequestDispatcher dispatcher;

    @BeforeEach
    public void setUp() {
        registry = new CollectorRegistry();
        behavior = new PrometheusMetricsBehavior(registry);

        RequestHandlerProvider requestHandlerProvider = new DefaultRequestHandlerProvider();
        requestHandlerProvider.register(new PingHandler());

        dispatcher = new RequestDispatcherImpl(requestHandlerProvider, Collections.singletonList(behavior));
    }

    @Test
    void shouldTrackCounts() {
        dispatcher.send(new Ping());
        dispatcher.send(new Ping());

        int count = registry.getSampleValue(
            "request_time_count",
            new String[]{"request_name"},
            new String[]{Ping.class.getName()}
        ).intValue();

        assertEquals(2, count);
    }

    @Test
    void shouldTrackTime() {
        dispatcher.send(new Ping());

        Double time = registry.getSampleValue(
            "request_time_sum",
            new String[]{"request_name"},
            new String[]{Ping.class.getName()});

        assertEquals(.5, time, .1);
    }

    private static class Ping implements Request {
        Ping() { }

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
