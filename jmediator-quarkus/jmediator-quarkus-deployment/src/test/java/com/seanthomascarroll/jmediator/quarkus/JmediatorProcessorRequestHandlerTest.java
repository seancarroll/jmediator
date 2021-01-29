package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JmediatorProcessorRequestHandlerTest {

    @RegisterExtension
    static final QuarkusUnitTest CONFIG = new QuarkusUnitTest()
        .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    HelloRequestHandler handler;

    @Test
    void shouldRegisterRequestHandler() {
        assertNotNull(handler);
    }

    static class HelloRequest implements Request {

        public String getName() {
            return "name";
        }

    }

    static class HelloRequestHandler implements RequestHandler<HelloRequest, String> {

        @Override
        public String handle(HelloRequest request) {
            return "Hello " + request.getName();
        }

    }

}
