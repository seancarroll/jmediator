package com.seanthomascarroll.jmediator.sample.micronaut;

import com.seanthomascarroll.jmediator.RequestHandler;

import jakarta.inject.Singleton;

@Singleton
public class HelloRequestHandler implements RequestHandler<HelloRequest, String> {

    @Override
    public String handle(HelloRequest request) {
        return "Hello " + request.getName();
    }

}
