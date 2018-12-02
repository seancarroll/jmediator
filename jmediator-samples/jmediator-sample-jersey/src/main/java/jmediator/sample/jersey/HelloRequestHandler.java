package jmediator.sample.jersey;

import jmediator.RequestHandler;

public class HelloRequestHandler implements RequestHandler<HelloRequest, String> {

    @Override
    public String handle(HelloRequest request) {
        return "Hello " + request.getName();
    }

}
