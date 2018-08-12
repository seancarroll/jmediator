package jmediator.jersey;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import jmediator.RequestHandler;


public class InjectTest {

    @Test
    public void registration() {

        RequestHandlerProviderImpl provider = new RequestHandlerProviderImpl("jmediator.jersey");

        provider.contextInitialized(null);

        RequestHandler handler = provider.getRequestHandler(new Ping());

        assertNotNull(handler);

    }

}
