package com.seanthomascarroll.jmediator.jersey;

import com.seanthomascarroll.jmediator.RequestDispatcher;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/hello")
public class HelloResource {

    private final RequestDispatcher dispatcher;

    @Inject
    public HelloResource(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @POST
    @Produces("application/json")
    public String hello(HelloRequest request) {
        return dispatcher.send(request);
    }

    @Path(("/missing"))
    @POST
    @Produces("application/json")
    public String missing(Missing request) {
        return dispatcher.send(request);
    }
}
