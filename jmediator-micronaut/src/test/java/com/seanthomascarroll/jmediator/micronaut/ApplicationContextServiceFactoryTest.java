package com.seanthomascarroll.jmediator.micronaut;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.seanthomascarroll.jmediator.NoHandlerForRequestException;
import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.behaviors.LoggingPipelineBehavior;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.inject.annotation.AnnotationMetadataSupport;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationContextServiceFactoryTest {

// https://docs.micronaut.io/latest/guide/
//    @Test
//    public void testEventListenerIsNotified() {
//        try (ApplicationContext context = ApplicationContext.run()) {
//            SampleEventEmitterBean emitter = context.getBean(SampleEventEmitterBean.class);
//            SampleEventListener listener = context.getBean(SampleEventListener.class);
//            assertEquals(0, listener.getInvocationCounter());
//            emitter.publishSampleEvent();
//            assertEquals(1, listener.getInvocationCounter());
//        }
//    }

    @Test
    void shouldSuccessfullyGetRegisteredRequestHandler() {
        ApplicationContext context = ApplicationContext.run();

        ApplicationContextServiceFactory serviceFactory = new ApplicationContextServiceFactory(context);

        context.registerSingleton(RequestHandler.class, new HelloRequestHandler());
//        context.registerSingleton(ApplicationEventListener.class, serviceFactory);

        serviceFactory.onApplicationEvent(new StartupEvent(context));


        RequestHandler<HelloRequest, String> handler = serviceFactory.getRequestHandler(HelloRequest.class);
        assertNotNull(handler);
        assertTrue(handler instanceof HelloRequestHandler);
    }

    @Test
    void shouldThrowForRequestClassThatHasNoRegisteredHandler() {
        ApplicationContext context = ApplicationContext.run();
        ApplicationContextServiceFactory serviceFactory = new ApplicationContextServiceFactory(context);
        serviceFactory.onApplicationEvent(new StartupEvent(context));

//        context.registerSingleton(ApplicationEventListener.class, serviceFactory);

        assertThrows(NoHandlerForRequestException.class, () -> serviceFactory.getRequestHandler(MissingRequest.class));
    }

    @Test
    void shouldWarnWhenMultipleHandlersAreAssociatedToRequest() {
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        Logger applicationContextServiceFactoryLogger = (Logger) LoggerFactory.getLogger(ApplicationContextServiceFactory.class);
        applicationContextServiceFactoryLogger.addAppender(listAppender);

        ApplicationContext context = ApplicationContext.run();

        ApplicationContextServiceFactory serviceFactory = new ApplicationContextServiceFactory(context);

        context.registerSingleton(RequestHandler.class, new MultipleHandlersOne());
        context.registerSingleton(RequestHandler.class, new MultipleHandlersTwo());
//        context.registerSingleton(ApplicationEventListener.class, serviceFactory);

        serviceFactory.onApplicationEvent(new StartupEvent(context));


        RequestHandler<MultipleHandlersRequest, String> handler = serviceFactory.getRequestHandler(MultipleHandlersRequest.class);
        assertNotNull(handler);

        assertEquals(1, listAppender.list.size());
        assertTrue(listAppender.list.get(0).getFormattedMessage().contains("MultipleHandlersRequest already associated with"));
        assertEquals(Level.WARN, listAppender.list.get(0).getLevel());
    }

    @Test
    void shouldSuccessfullyGetRegisteredPipelineBehavior() {
        ApplicationContext context = ApplicationContext.run();
        ApplicationContextServiceFactory serviceFactory = new ApplicationContextServiceFactory(context);

        context.registerSingleton(PipelineBehavior.class, new LoggingPipelineBehavior());
//        context.registerSingleton(ApplicationEventListener.class, serviceFactory);

        serviceFactory.onApplicationEvent(new StartupEvent(context));


        List<PipelineBehavior> behaviors = serviceFactory.getPipelineBehaviors();
        assertNotNull(behaviors);
        assertEquals(1, behaviors.size());
        assertTrue(behaviors.get(0) instanceof LoggingPipelineBehavior);
    }

    static class HelloRequest implements Request {

        public String getName() {
            return "name";
        }

    }

    @Singleton
    static class HelloRequestHandler implements RequestHandler<HelloRequest, String> {

        @Override
        public String handle(HelloRequest request) {
            return "Hello " + request.getName();
        }

    }

    static class MissingRequest implements Request {

    }

    static class MultipleHandlersRequest implements Request {

    }

    static class MultipleHandlersOne implements RequestHandler<MultipleHandlersRequest, String> {

        @Override
        public String handle(MultipleHandlersRequest request) {
            return "one";
        }
    }

    static class MultipleHandlersTwo implements RequestHandler<MultipleHandlersRequest, String> {

        @Override
        public String handle(MultipleHandlersRequest request) {
            return "two";
        }
    }

}
