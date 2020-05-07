package com.seanthomascarroll.jmediator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultServiceFactoryTest {

    @Test
    void shouldRequestRequestHandler() {
        DefaultServiceFactory requestHandlerProvider = new DefaultServiceFactory();

        requestHandlerProvider.register(new SomeRequestHandler());

        assertNotNull(requestHandlerProvider.getRequestHandler(SomeRequest.class));
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
