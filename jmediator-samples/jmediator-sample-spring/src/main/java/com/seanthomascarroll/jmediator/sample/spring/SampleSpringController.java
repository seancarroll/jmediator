package com.seanthomascarroll.jmediator.sample.spring;

import com.seanthomascarroll.jmediator.RequestDispatcher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleSpringController {

    private final RequestDispatcher dispatcher;

    public SampleSpringController(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String hello(HelloRequest request) {
        return dispatcher.send(request);
    }
}
