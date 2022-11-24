package com.seanthomascarroll.jmediator.spring;

import com.seanthomascarroll.jmediator.RequestHandler;
import jakarta.inject.Named;

@Named
public class PingHandler implements RequestHandler<Ping, String> {
    @Override
    public String handle(Ping request) {
        return "pong";
    }
}
