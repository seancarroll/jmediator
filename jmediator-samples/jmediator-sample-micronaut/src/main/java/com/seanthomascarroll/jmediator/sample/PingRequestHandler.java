package com.seanthomascarroll.jmediator.sample;

import com.seanthomascarroll.jmediator.RequestHandler;

import javax.inject.Singleton;

@Singleton
public class PingRequestHandler implements RequestHandler<PingRequest, String> {

    @Override
    public String handle(PingRequest request) {
        return "Pong";
    }

}
