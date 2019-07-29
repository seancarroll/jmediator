package jmediator.quarkus;

import jmediator.RequestHandler;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped()
public class PingHandler implements RequestHandler<PingRequest, Pong> {

    @Override
    public Pong handle(PingRequest request) {
        return new Pong(request.getValue());
    }
}
