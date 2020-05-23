package com.seanthomascarroll.jmediator.sample.quarkus;

import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.pipeline.behaviors.LoggingPipelineBehavior;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource {

    private final RequestDispatcher dispatcher;

    public HelloResource(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getHello() {
        return "Hello";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(HelloRequest request) {
        return dispatcher.send(request);
    }

}
