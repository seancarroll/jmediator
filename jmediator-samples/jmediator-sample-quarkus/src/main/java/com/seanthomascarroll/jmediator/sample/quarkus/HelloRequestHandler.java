package com.seanthomascarroll.jmediator.sample.quarkus;

import com.seanthomascarroll.jmediator.RequestHandler;
import io.quarkus.arc.Unremovable;

import javax.inject.Singleton;

@Unremovable
@Singleton
public class HelloRequestHandler implements RequestHandler<HelloRequest, String> {

    @Override
    public String handle(HelloRequest request) {
        return "Hello " + request.getName();
    }
}
