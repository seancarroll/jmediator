package com.seanthomascarroll.jmediator.jersey;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// TODO: I really wish I didnt have to use JerseyTest to unit test JmediatorFeature
// however I havent found a good way to just test JmediatorFeature in isolation.
// The two big things are how to create an appropriate FeatureContext and make sure we
// get an appropriate injection manager.
public class JmediatorFeatureTest extends JerseyTest {

    @Override
    protected Application configure() {
        ResourceConfig resourceConfig = new ResourceConfig(HelloResource.class);
        resourceConfig.register(new JmediatorFeature("com.seanthomascarroll.jmediator.jersey"));
        resourceConfig.register(NoHandlerFoundExceptionMapper.class);

        return resourceConfig;
    }

    @Test
    public void shouldExecuteHandler() {
        HelloRequest request = new HelloRequest("Sean");

        Response response = target("/hello")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));

        System.out.println(response);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        String content = response.readEntity(String.class);
        assertEquals("Hello Sean", content);
    }

    @Test
    public void shouldThrowForRequestClassThatHasNoRegisteredHandler() {
        Missing request = new Missing();

        Response response = target("/hello/missing")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.readEntity(String.class).contains("request handler bean not registered for class"));
    }

}
