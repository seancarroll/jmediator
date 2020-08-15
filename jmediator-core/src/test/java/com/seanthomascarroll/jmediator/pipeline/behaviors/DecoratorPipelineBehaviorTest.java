package com.seanthomascarroll.jmediator.pipeline.behaviors;

import com.seanthomascarroll.jmediator.DefaultServiceFactory;
import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecoratorPipelineBehaviorTest {

    @Test
    void shouldBeAbleToDecorateRequest() {
        DefaultServiceFactory serviceFactory = new DefaultServiceFactory();
        serviceFactory.register(new PingRequestHandler());
        serviceFactory.register(new DecoratorPipelineBehavior());

        RequestDispatcherImpl dispatcher = new RequestDispatcherImpl(serviceFactory);

        String value = dispatcher.send(new Ping());

        assertEquals("decorated", value);
    }

    static class DecoratorPipelineBehavior implements PipelineBehavior {

        @Override
        public Object handle(Request request, PipelineChain chain) {
            Request decorator = new DecoratorRequest<>("decorated", request);
            return chain.doBehavior(decorator);
        }

    }

    static class Ping implements Request {

    }

    static class DecoratorRequest<T extends Request> implements Request {

        // TODO: update type to appropriate type
        private final String someValue;
        private final T innerRequest;

        public DecoratorRequest(String someValue, T innerRequest) {
            this.someValue = someValue;
            this.innerRequest = innerRequest;
        }

        public String getSomeValue() {
            return someValue;
        }

        public T getInnerRequest() {
            return innerRequest;
        }
    }

    static class PingRequestHandler implements RequestHandler<DecoratorRequest<Ping>, String> {

        @Override
        public String handle(DecoratorRequest<Ping> request) {
            return request.someValue;
        }
    }

}
