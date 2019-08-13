package com.seanthomascarroll.jmediator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultRequestHandlerProviderTest {

    @Test
    void shouldRequestRequestHandler() {
        DefaultRequestHandlerProvider requestHandlerProvider = new DefaultRequestHandlerProvider();

        requestHandlerProvider.register(new SomeRequestHandler());

        assertNotNull(requestHandlerProvider.getRequestHandler(new SomeRequest()));
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
