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

//    private static final PeopleStore dao = mock(PeopleStore.class);
//
//    @ClassRule
//    public static final ResourceTestRule resources = ResourceTestRule.builder()
//        .addResource(new PersonResource(dao))
//        .build();

//    private final Person person = new Person("blah", "blah@example.com");

//    private static final RequestDispatcher dispatcher = new RequestDispatcherImpl(new RequestHandlerProviderImpl());

//    public final ResourceTestRule resources = ResourceTestRule.builder()
//        .addResource(new HelloResource(dispatcher))
//        .build();

//    public static final ResourceExtension RESOURCES = ResourceExtension.builder()
//        .addResource(new HelloResource(dispatcher))
//        .build();

    public static final DropwizardAppExtension<SampleDropwizardConfiguration> app = new DropwizardAppExtension<>(SampleDropwizardApplication.class);



//    public static final DropwizardAppRule<SampleDropwizardConfiguration> RULE =
//        new DropwizardAppRule<>(SampleDropwizardApplication.class, ResourceHelpers.resourceFilePath("sample.yaml"));

    @BeforeEach
    public void setup() {
//        when(dao.fetchPerson(eq("blah"))).thenReturn(person);
    }

    @AfterEach
    public void tearDown(){
        // we have to reset the mock after each test because of the
        // @ClassRule, or use a @Rule as mentioned below.
//        reset(dao);
    }

//    @Test
//    public void testGetPerson() {
//        assertThat(resources.target("/person/blah").request().get(Person.class))
//            .isEqualTo(person);
//        verify(dao).fetchPerson("blah");
//    }

    @Test
    void helloShouldReturnMessage() {
        HelloRequest request = new HelloRequest();
        request.setName("Sean");

        final Response response = app.client().target("http://localhost:" + app.getLocalPort() + "/")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));

//        final Response response = RULE.client().target("http://localhost:" + RULE.getLocalPort() + "/")
//            .request(MediaType.APPLICATION_JSON_TYPE)
//            .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));

//        final Response response = RESOURCES.target("/").request(MediaType.APPLICATION_JSON_TYPE)
//            .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Hello Sean", response.readEntity(String.class));

//        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
//        verify(PERSON_DAO).create(personCaptor.capture());
//        assertThat(personCaptor.getValue()).isEqualTo(person);
    }

}
