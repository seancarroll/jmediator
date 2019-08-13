package com.seanthomascarroll.jmediator.sample;

import com.seanthomascarroll.jmediator.RequestDispatcher;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;

@Controller()
public class SampleMicronautController {

    private final RequestDispatcher dispatcher;

    public SampleMicronautController(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    // https://github.com/micronaut-projects/micronaut-core/blob/master/src/main/docs/guide/httpServer/binding.adoc
    // Using the syntax {?sorting*} we can assign request parameters to a POJO,
    // Also, in order to bind all request URI variables or request parameters to a command object, you can define URI route variable as ?pojo*. For example:
    // TODO: how to avoid having to specify path variable and just bind to object?
    @Get(uri = "/hello/{name}", produces = MediaType.APPLICATION_JSON)
    public String hello(@PathVariable(name = "name") String name) {
        HelloRequest request = new HelloRequest(name);
        return dispatcher.send(request);
    }
}
