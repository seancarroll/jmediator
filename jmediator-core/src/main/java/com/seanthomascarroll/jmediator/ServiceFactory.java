package com.seanthomascarroll.jmediator;

import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;

import java.util.List;

public interface ServiceFactory {

    /**
     * 
     * @param requestClass
     * @param <T>
     * @param <R>
     * @return
     */
    <T extends Request, R> RequestHandler<T, R> getRequestHandler(Class<? extends Request> requestClass);

    /**
     *
     * @return
     */
    List<PipelineBehavior> getPipelineBehaviors();

    /**
     *
     * @param handles
     */
    default void release(List<Object> handles) {
        // no-op default implementation
    }
}
