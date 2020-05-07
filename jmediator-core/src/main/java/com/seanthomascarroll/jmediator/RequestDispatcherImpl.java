package com.seanthomascarroll.jmediator;

import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import com.seanthomascarroll.jmediator.pipeline.PipelineChainImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the dispatcher, aka command bus, that dispatches requests to the handlers subscribed to the specific type of request.
 * Pipeline behaviors may be configured to add processing to requests regardless of their type
 */
public class RequestDispatcherImpl implements RequestDispatcher {

//    private final RequestHandlerProvider requestHandlerProvider;
//    private final List<PipelineBehavior> pipelineBehaviors;
    private final ServiceFactory serviceFactory;

    public RequestDispatcherImpl(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

//    /**
//     * @param requestHandlerProvider
//     */
//    public RequestDispatcherImpl(RequestHandlerProvider requestHandlerProvider) {
//        this(requestHandlerProvider, Collections.emptyList());
//    }
//
//    /**
//     * @param requestHandlerProvider
//     * @param pipelineBehaviors
//     */
//    public RequestDispatcherImpl(RequestHandlerProvider requestHandlerProvider, List<PipelineBehavior> pipelineBehaviors) {
//        this.requestHandlerProvider = Ensure.notNull(requestHandlerProvider);
//        this.pipelineBehaviors = Ensure.notNull(pipelineBehaviors);
//    }

    @Override
    public <R> R send(Request request) {
        return doSend(request);
    }

    @SuppressWarnings({"unchecked"})
    private <T extends Request, R> R doSend(T request) {
        RequestHandler<? super T, ?> handler = serviceFactory.getRequestHandler(request.getClass());
        List<PipelineBehavior> pipelineBehaviors = serviceFactory.getPipelineBehaviors();

        PipelineChain chain = new PipelineChainImpl<>(request, pipelineBehaviors, handler);

        return (R) chain.doBehavior();
    }

}
