package com.seanthomascarroll.jmediator.pipeline.honeycomb;

import com.seanthomascarroll.jmediator.RequestHandler;

public class PingHandler implements RequestHandler<Ping, Pong> {

    @Override
    public Pong handle(Ping request) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // ignore
        }

        return new Pong("hello " + request.message);
    }
}
