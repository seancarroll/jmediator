package com.seanthomascarroll.jmediator.sample.quarkus;

import com.seanthomascarroll.jmediator.RequestHandler;

import javax.inject.Singleton;

@Singleton
public class HelloRequestHandler implements RequestHandler<HelloRequest, String> {

    @Override
    public String handle(HelloRequest request) {
        return "Hello " + request.getName();
    }
}
