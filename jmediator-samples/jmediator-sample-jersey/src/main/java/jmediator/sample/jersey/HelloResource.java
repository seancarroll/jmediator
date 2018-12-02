package jmediator.sample.jersey;

import jmediator.RequestDispatcher;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class HelloResource {


    private final RequestDispatcher dispatcher;

    public HelloResource(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @POST
    @Produces("application/json")
    public String hello(HelloRequest request) {
        return dispatcher.send(request);
    }
}
