package com.seanthomascarroll.jmediator.pipeline;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;

/**
 * The pipeline chain manages the flow of a message through a chain of behaviors and ultimately to the request handler.
 * Behaviors may continue processing via this chain by calling the {@link #doBehavior(Request)} method.
 * Alternatively, they can block processing by returning without calling either of these methods.
 */
public interface PipelineChain<T extends Request> {

    /**
     *
     * @param request
     * @return the result from a request handler
     */
    Object doBehavior(T request);

    /**
     *
     * @return  the handler to be called at the end of the pipeline chain
     */
    RequestHandler<T, ?> getHandler();

}
