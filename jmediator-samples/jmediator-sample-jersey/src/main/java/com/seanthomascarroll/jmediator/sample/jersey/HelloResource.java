package com.seanthomascarroll.jmediator.sample.jersey;

import com.seanthomascarroll.jmediator.RequestDispatcher;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
//import jakarta.inject.Inject;
//import jakarta.ws.rs.POST;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.Produces;

@Path("/")
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
}
