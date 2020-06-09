package com.seanthomascarroll.jmediator.micronaut;

import com.seanthomascarroll.jmediator.NoHandlerForRequestException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationContextServiceFactoryTest {

    @Test
    void shouldSuccessfullyGetRegisteredRequestHandler() {
        ApplicationContext context = ApplicationContext.run();
        context.registerSingleton(RequestHandler.class, new HelloRequestHandler());
        context.registerSingleton(ApplicationEventListener.class, new ApplicationContextServiceFactory(context));

        ApplicationContextServiceFactory serviceFactory = new ApplicationContextServiceFactory(context);
        serviceFactory.onApplicationEvent(new StartupEvent(context));

        RequestHandler<HelloRequest, String> handler = serviceFactory.getRequestHandler(HelloRequest.class);
        assertNotNull(handler);
        assertTrue(handler instanceof HelloRequestHandler);
    }

    @Test
    void shouldThrowForRequestClassThatHasNoRegisteredHandler() {
        ApplicationContext context = ApplicationContext.run();
        context.registerSingleton(ApplicationEventListener.class, new ApplicationContextServiceFactory(context));

        ApplicationContextServiceFactory serviceFactory = new ApplicationContextServiceFactory(context);
        serviceFactory.onApplicationEvent(new StartupEvent(context));

        assertThrows(NoHandlerForRequestException.class, () -> serviceFactory.getRequestHandler(MissingRequest.class));
    }

    @Test
    void shouldSuccessfullyGetRegisteredPipelineBehavior() {
        ApplicationContext context = ApplicationContext.run();
        context.registerSingleton(PipelineBehavior.class, new LoggingPipelineBehavior());
        context.registerSingleton(ApplicationEventListener.class, new ApplicationContextServiceFactory(context));

        ApplicationContextServiceFactory requestHandlerProvider = new ApplicationContextServiceFactory(context);

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

    static class MissingRequest implements Request {

    }

}
