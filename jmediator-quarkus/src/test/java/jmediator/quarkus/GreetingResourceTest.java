package jmediator.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import jmediator.NoHandlerForRequestException;
import jmediator.Request;
import jmediator.RequestHandler;
import jmediator.RequestHandlerProvider;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest()
class GreetingResourceTest {

    @Inject
    RequestHandlerProvider requestHandlerProvider;

    @Test
    void shouldObtainRequestHandlerForRequestThatHasHandler() {
        RequestHandler<Request, Object> handler = requestHandlerProvider.getRequestHandler(new PingRequest("Sean"));

        assertNotNull(handler);
    }

    @Test
    void shouldThrowNoHandlerForRequestExceptionForTypeThatHasNoHandler() {
        assertThrows(NoHandlerForRequestException.class, () -> requestHandlerProvider.getRequestHandler(new NoHandler()));
    }
}
