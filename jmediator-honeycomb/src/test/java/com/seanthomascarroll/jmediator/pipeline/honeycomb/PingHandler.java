package com.seanthomascarroll.jmediator.pipeline.honeycomb;

import com.seanthomascarroll.jmediator.RequestHandler;

import java.util.concurrent.TimeUnit;

public class PingHandler implements RequestHandler<Ping, Pong> {

    @Override
    public Pong handle(Ping request) {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            // ignore
        }

        return new Pong("hello " + request.message);
    }
}
