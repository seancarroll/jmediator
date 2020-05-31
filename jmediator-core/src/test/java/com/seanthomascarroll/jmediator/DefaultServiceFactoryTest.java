package com.seanthomascarroll.jmediator;

import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.behaviors.LoggingPipelineBehavior;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultServiceFactoryTest {

    @Test
    void shouldSuccessfullyRegisterRequestHandler() {
        DefaultServiceFactory serviceFactory = new DefaultServiceFactory();

        serviceFactory.register(new SomeRequestHandler());

        assertNotNull(serviceFactory.getRequestHandler(SomeRequest.class));
    }

    @Test
    void shouldSuccessfullyRegisterPipelineBehavior() {
        DefaultServiceFactory serviceFactory = new DefaultServiceFactory();

        serviceFactory.register(new LoggingPipelineBehavior());

        List<PipelineBehavior> behaviors = serviceFactory.getPipelineBehaviors();

        assertEquals(1, behaviors.size());
        assertTrue(behaviors.get(0) instanceof LoggingPipelineBehavior);
    }


    static class SomeRequest implements Request {
    }

    static class SomeRequestHandler implements RequestHandler<SomeRequest, Void> {

        @Override
        public Void handle(SomeRequest request) {
            return null;
        }
    }
}
