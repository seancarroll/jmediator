package jmediator.sample;

import jmediator.RequestHandler;

import javax.inject.Singleton;

@Singleton
public class PingRequestHandler implements RequestHandler<PingRequest, String> {

    @Override
    public String handle(PingRequest request) {
        return "Pong";
    }

}
