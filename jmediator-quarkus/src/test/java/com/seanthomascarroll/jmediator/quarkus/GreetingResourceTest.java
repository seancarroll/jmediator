package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.NoHandlerForRequestException;
import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.ServiceFactory;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest()
class GreetingResourceTest {

    @Inject
    ServiceFactory serviceFactory;

    @Test
    void shouldObtainRequestHandlerForRequestThatHasHandler() {
        RequestHandler<Request, Object> handler = serviceFactory.getRequestHandler(PingRequest.class);

        assertNotNull(handler);
    }

    @Test
    void shouldThrowNoHandlerForRequestExceptionForTypeThatHasNoHandler() {
        assertThrows(NoHandlerForRequestException.class, () -> serviceFactory.getRequestHandler(NoHandler.class));
    }
}
