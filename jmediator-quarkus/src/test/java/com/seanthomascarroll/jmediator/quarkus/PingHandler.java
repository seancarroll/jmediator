package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.RequestHandler;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped()
public class PingHandler implements RequestHandler<PingRequest, Pong> {

    @Override
    public Pong handle(PingRequest request) {
        return new Pong(request.getValue());
    }
}
