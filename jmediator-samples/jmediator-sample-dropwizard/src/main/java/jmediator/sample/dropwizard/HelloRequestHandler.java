package jmediator.sample.dropwizard;

import jmediator.RequestHandler;

import javax.inject.Named;

@Named
public class HelloRequestHandler implements RequestHandler<HelloRequest, String> {

    @Override
    public String handle(HelloRequest request) {
        return "Hello " + request.getName();
    }

}
