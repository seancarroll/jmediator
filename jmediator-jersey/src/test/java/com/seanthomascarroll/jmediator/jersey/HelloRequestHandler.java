package com.seanthomascarroll.jmediator.jersey;

import com.seanthomascarroll.jmediator.RequestHandler;
import jakarta.inject.Named;

@Named
public class HelloRequestHandler implements RequestHandler<HelloRequest, String> {

    @Override
    public String handle(HelloRequest request) {
        return "Hello " + request.getName();
    }
}
