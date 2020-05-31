package com.seanthomascarroll.jmediator;

import com.seanthomascarroll.jmediator.pipeline.behaviors.LoggingPipelineBehavior;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestDispatcherImplTest {

    @Test
    void shouldSuccessfullySendRequest() {
        DefaultServiceFactory serviceFactory = new DefaultServiceFactory();
        serviceFactory.register(new PingRequestHandler());
        serviceFactory.register(new LoggingPipelineBehavior());

        RequestDispatcherImpl dispatcher = new RequestDispatcherImpl(serviceFactory);

        String value = dispatcher.send(new Ping());

        assertEquals("pong", value);
    }

    static class Ping implements Request {
    }

    static class PingRequestHandler implements RequestHandler<Ping, String> {

        @Override
        public String handle(Ping request) {
            return "pong";
        }
    }
}
