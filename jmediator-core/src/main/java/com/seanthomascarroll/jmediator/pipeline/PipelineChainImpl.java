package com.seanthomascarroll.jmediator.pipeline;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;

import java.util.List;

/**
 * Mechanism that takes care of behavior and handler execution.
 *
 * @param <T>  The message type this pipeline chain can process
 */
public class PipelineChainImpl implements PipelineChain {

    private final List<? extends PipelineBehavior> behaviors;
    private final RequestHandler<? super Request, ?> handler;
    private int position = 0;

    /**
     * Initialize the default pipeline chain to dispatch the given {@code request}, through the
     * {@code behaviors}, to the {@code handler}.
     *
     * @param behaviors  The behaviors composing the chain
     * @param handler  The handler for the request
     */
    public PipelineChainImpl(List<? extends PipelineBehavior> behaviors, RequestHandler<? super Request, ?> handler) {
        this.behaviors = behaviors;
        this.handler = handler;
    }


    @Override
    public Object doBehavior(Request request) {
        if (position < behaviors.size()) {
            return behaviors.get(position++).handle(request, this);
        } else {
            return handler.handle(request);
        }
    }

    @Override
    public RequestHandler<? super Request, ?> getHandler() {
        return handler;
    }

}
