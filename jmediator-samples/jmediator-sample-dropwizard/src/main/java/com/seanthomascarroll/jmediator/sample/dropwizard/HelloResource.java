package com.seanthomascarroll.jmediator.sample.dropwizard;

import com.codahale.metrics.annotation.Timed;
import com.seanthomascarroll.jmediator.RequestDispatcher;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.inject.Inject;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {
    private final RequestDispatcher dispatcher;

    @Inject
    public HelloResource(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @POST
    @Timed
    public String hello(HelloRequest request) {
        return dispatcher.send(request);
    }
}
