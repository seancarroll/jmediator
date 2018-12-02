package jmediator.sample.dropwizard;

import com.codahale.metrics.annotation.Timed;
import jmediator.RequestDispatcher;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {
    private final RequestDispatcher dispatcher;

    public HelloResource(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @POST
    @Timed
    public String hello(HelloRequest request) {
        return dispatcher.send(request);
    }
}
