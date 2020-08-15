package com.seanthomascarroll.jmediator.pipeline;

import com.seanthomascarroll.jmediator.Request;

/**
 * Pipeline behavior that surrounds an inner handler.
 * Implementations add additional behavior and call next
 */
public interface PipelineBehavior {

    /**
     * @param request  The request to be processed
     * @param chain  The chain manages the flow of a message through a chain of behaviors and ultimately to the request
     *               handler
     * @param <T>  The message type this behavior chain can process
     * @return Response from the handler
     */
    <T extends Request> Object handle(Request request, PipelineChain chain);
}
