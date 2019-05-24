package jmediator.sample;

import jmediator.RequestHandler;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class HelloRequestHandler implements RequestHandler<HelloRequest, String> {

    @Override
    public String handle(HelloRequest request) {
        return "Hello " + request.getName();
    }

}
