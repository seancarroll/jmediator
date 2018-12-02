package jmediator.dropwizard.fake.entities.pckg;

import jmediator.RequestHandler;

public class FakeRequestHandler implements RequestHandler<FakeRequest, Object> {

    @Override
    public Object handle(FakeRequest request) {
        return null;
    }

}
