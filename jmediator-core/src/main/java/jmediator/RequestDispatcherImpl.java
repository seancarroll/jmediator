package jmediator;

import jmediator.pipeline.PipelineBehavior;
import jmediator.pipeline.PipelineChain;
import jmediator.pipeline.PipelineChainImpl;

import java.util.List;

/**
 * Implementation of the dispatcher, aka command bus, that dispatches requests to the handlers subscribed to the specific type of request.
 * Pipeline behaviors may be configured to add processing to requests regardless of their type
 *
 */
public class RequestDispatcherImpl implements RequestDispatcher {

    private final RequestHandlerProvider requestHandlerProvider;

    // TODO: add register/subscribe method
    private final List<PipelineBehavior> handlerInterceptors;

    public RequestDispatcherImpl(RequestHandlerProvider requestHandlerProvider, List<PipelineBehavior> handlerInterceptors) {
        this.requestHandlerProvider = requestHandlerProvider;
        this.handlerInterceptors = handlerInterceptors;
    }

    @Override
    public <R> R send(Request request) {
        return doSend(request);
    }

    @SuppressWarnings({ "unchecked"})
	private <T extends Request, R> R doSend(T request) {
        RequestHandler<? super T, ?> handler = requestHandlerProvider.getRequestHandler(request);

        PipelineChain chain = new PipelineChainImpl<>(request, handlerInterceptors, handler);

        return (R) chain.doBehavior();
    }


}
