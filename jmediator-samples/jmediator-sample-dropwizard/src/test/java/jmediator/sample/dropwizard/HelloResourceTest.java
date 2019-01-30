package jmediator.sample.dropwizard;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import jmediator.RequestDispatcher;
import jmediator.RequestDispatcherImpl;
import jmediator.dropwizard.RequestHandlerProviderImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;

// https://jersey.github.io/documentation/latest/test-framework.html
@ExtendWith(DropwizardExtensionsSupport.class)
class HelloResourceTest {

    public static final DropwizardAppExtension<SampleDropwizardConfiguration> app = new DropwizardAppExtension<>(SampleDropwizardApplication.class);

    @Test
    void helloShouldReturnMessage() {
        HelloRequest request = new HelloRequest();
        request.setName("Sean");

        final Response response = app.client().target("http://localhost:" + app.getLocalPort() + "/")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Hello Sean", response.readEntity(String.class));

    }

}
