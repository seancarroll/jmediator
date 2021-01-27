package com.seanthomascarroll.jmediator.sample.jersey;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

// https://stackoverflow.com/questions/28436040/hk2-is-not-injecting-the-httpservletrequest-with-jersey/28437888#28437888
// https://stackoverflow.com/questions/30896911/jersey-junit-test-weblistener-servletcontextlistener-not-invoked
// https://github.com/jersey/jersey/issues/3386
// http://generationpalindrome.blogspot.com/2012/06/jerseytest-with-servletcontextlistener.html
// Using JUnit 4 because of JerseyTest
public class HelloResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        //return new ResourceConfig(HelloResource.class);
        return new AppConfig();
    }

    @Test
    public void helloShouldReturnMessage() {
        HelloRequest request = new HelloRequest();
        request.setName("Sean");

        Response response = target("/").request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        String content = response.readEntity(String.class);
        assertEquals("Hello Sean", content);
    }

}
