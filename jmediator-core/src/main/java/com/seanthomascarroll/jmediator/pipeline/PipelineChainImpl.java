package com.seanthomascarroll.jmediator.pipeline;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;

import java.util.List;

/**
 * Mechanism that takes care of behavior and handler execution.
 *
 * @param <T>  The message type this pipeline chain can process
 */
public class PipelineChainImpl<T extends Request> implements PipelineChain {

    private final T request;
    private final List<? extends PipelineBehavior> behaviors;
    private final RequestHandler<? super T, ?> handler;
    private int position = 0;

    /**
     * Initialize the default pipeline chain to dispatch the given {@code request}, through the
     * {@code behaviors}, to the {@code handler}.
     *
     * @param request  The request to be processed
     * @param behaviors  The behaviors composing the chain
     * @param handler  The handler for the request
     */
    public PipelineChainImpl(T request, List<? extends PipelineBehavior> behaviors, RequestHandler<? super T, ?> handler) {
        this.request = request;
        this.behaviors = behaviors;
        this.handler = handler;
    }


    @Override
    public Object doBehavior() {
        if (position < behaviors.size()) {
            return behaviors.get(position++).handle(request, this);
        } else {
            return handler.handle(request);
        }
    }

}
