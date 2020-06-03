package com.seanthomascarroll.jmediator.pipeline.honeycomb;

import com.seanthomascarroll.jmediator.DefaultServiceFactory;
import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import io.honeycomb.libhoney.HoneyClient;
import io.honeycomb.libhoney.LibHoney;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HoneycombMetricsBehaviorTest {

    private static final String WRITE_KEY = "test-write-key";
    private static final String DATASET   = "test-dataset";

    @Test
    void shouldTrackMetrics() {

        HoneyClient client = LibHoney.create(LibHoney.options().setDataset(DATASET).setWriteKey(WRITE_KEY).build());
        HoneycombContext context = new HoneycombContext(client);

        DefaultServiceFactory serviceFactory = new DefaultServiceFactory();
        serviceFactory.register(new PingHandler());
        serviceFactory.register(new HoneycombMetricsBehavior(context));
        RequestDispatcher dispatcher = new RequestDispatcherImpl(serviceFactory);

        dispatcher.send(new Ping());

        long duration = (long) context.getEvent().getFields().get("timers." + Ping.class.getName());
        assertEquals(500, duration, 100);
    }

}
