package com.seanthomascarroll.jmediator;

import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import com.seanthomascarroll.jmediator.pipeline.PipelineChainImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of the dispatcher, aka command bus, that dispatches requests to the handlers subscribed to the specific type of request.
 * Pipeline behaviors may be configured to add processing to requests regardless of their type
 */
public class RequestDispatcherImpl implements RequestDispatcher {

    private final ServiceFactory serviceFactory;

    public RequestDispatcherImpl(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public <R> R send(Request request) {
        return doSend(request);
    }

    @SuppressWarnings({"unchecked"})
    private <T extends Request, R> R doSend(T request) {
        RequestHandler<? super T, ?> handler = serviceFactory.getRequestHandler(request.getClass());
        List<PipelineBehavior> pipelineBehaviors = serviceFactory.getPipelineBehaviors();

        PipelineChain chain = new PipelineChainImpl<>(request, pipelineBehaviors, handler);

        R response = (R) chain.doBehavior();

        release(handler, pipelineBehaviors);

        return response;
    }

    private void release(RequestHandler requestHandler, List<PipelineBehavior> behaviors) {
        List<Object> objects = new ArrayList<>(behaviors);
        objects.add(requestHandler);

        serviceFactory.release(objects);
    }

}
