package com.seanthomascarroll.jmediator.micronaut;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.behaviors.LoggingPipelineBehavior;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationContextRequestHandlerProviderTest {

    @Test
    void shouldSuccessfullyGetRegisteredRequestHandler() {
        ApplicationContext context = ApplicationContext.run();
        context.registerSingleton(RequestHandler.class, new HelloRequestHandler());
        context.registerSingleton(ApplicationEventListener.class, new ApplicationContextRequestHandlerProvider(context));

        ApplicationContextRequestHandlerProvider requestHandlerProvider = new ApplicationContextRequestHandlerProvider(context);
        requestHandlerProvider.onApplicationEvent(new StartupEvent(context));

        RequestHandler<HelloRequest, String> handler = requestHandlerProvider.getRequestHandler(HelloRequest.class);
        assertNotNull(handler);
        assertTrue(handler instanceof HelloRequestHandler);
    }

    @Test
    void shouldSuccessfullyGetRegisteredPipelineBehavior() {
        ApplicationContext context = ApplicationContext.run();
        context.registerSingleton(PipelineBehavior.class, new LoggingPipelineBehavior());
        context.registerSingleton(ApplicationEventListener.class, new ApplicationContextRequestHandlerProvider(context));

        ApplicationContextRequestHandlerProvider requestHandlerProvider = new ApplicationContextRequestHandlerProvider(context);

        List<PipelineBehavior> behaviors = requestHandlerProvider.getPipelineBehaviors();
        assertNotNull(behaviors);
        assertEquals(1, behaviors.size());
        assertTrue(behaviors.get(0) instanceof LoggingPipelineBehavior);
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
