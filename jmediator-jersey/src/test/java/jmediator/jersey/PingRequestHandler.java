package jmediator.jersey;

import jmediator.RequestHandler;

public class PingRequestHandler implements RequestHandler<Ping, Pong> {

    @Override
    public Pong handle(Ping request) {
        return new Pong();
    }
}
