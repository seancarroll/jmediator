package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.NoHandlerForRequestException;
import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.quarkus.arc.ArcContainer;
import io.quarkus.arc.InstanceHandle;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// TODO: would potentially be nice to use ArcTestContainer if Quarkus decides to deploy it
public class QuarkusServiceFactoryTest {

    @Test
    void shouldThrowWhenRequestNotRegistered() {
        Map<String, Class<? extends RequestHandler>> handlers = new HashMap<>();

        QuarkusServiceFactory serviceFactory = new QuarkusServiceFactory(handlers, null, null);

        assertThrows(NoHandlerForRequestException.class, () -> serviceFactory.getRequestHandler(HelloRequest.class));
    }

    @Test
    void shouldThrowWhenRequestHandlerInstanceIsNotFound() {
        InstanceHandle<HelloRequestHandler> instanceHandle = mock(InstanceHandle.class);
        when(instanceHandle.get()).thenReturn(null);

        ArcContainer container = mock(ArcContainer.class);
        when(container.instance(HelloRequestHandler.class)).thenReturn(instanceHandle);

        Map<String, Class<? extends RequestHandler>> handlers = new HashMap<>();
        handlers.put(HelloRequest.class.getName(), HelloRequestHandler.class);

        QuarkusServiceFactory serviceFactory = new QuarkusServiceFactory(handlers, null, container);

        assertThrows(NoHandlerForRequestException.class, () -> serviceFactory.getRequestHandler(HelloRequest.class));
    }

    @Test
    void shouldSuccessfullyReturnRequestHandler() {
        InstanceHandle<HelloRequestHandler> instanceHandle = mock(InstanceHandle.class);
        when(instanceHandle.get()).thenReturn(new HelloRequestHandler());

        ArcContainer container = mock(ArcContainer.class);
        when(container.instance(HelloRequestHandler.class)).thenReturn(instanceHandle);

        Map<String, Class<? extends RequestHandler>> handlers = new HashMap<>();
        handlers.put(HelloRequest.class.getName(), HelloRequestHandler.class);

        List<Class<? extends PipelineBehavior>> behaviors = Collections.singletonList(NoopBehavior.class);

        QuarkusServiceFactory serviceFactory = new QuarkusServiceFactory(handlers, behaviors, container);

        RequestHandler handler = serviceFactory.getRequestHandler(HelloRequest.class);

        assertNotNull(handler);
        assertTrue(handler instanceof HelloRequestHandler);
    }

    @Test
    void shouldSuccessfullyReturnBehaviors() {
        ArcContainer container = mock(ArcContainer.class);

        InstanceHandle<NoopBehavior> behaviorInstance = mock(InstanceHandle.class);
        when(behaviorInstance.get()).thenReturn(new NoopBehavior());
        when(container.instance(NoopBehavior.class)).thenReturn(behaviorInstance);

        QuarkusServiceFactory serviceFactory = new QuarkusServiceFactory(null, Collections.singletonList(NoopBehavior.class), container);

        List<PipelineBehavior> behaviors = serviceFactory.getPipelineBehaviors();

        assertEquals(1, behaviors.size());
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

    static class NoopBehavior implements PipelineBehavior {

        @Override
        public <T extends Request> Object handle(T request, PipelineChain chain) {
            return chain.doBehavior();
        }
    }
}
