package com.seanthomascarroll.jmediator;

import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;

import java.util.List;

public interface ServiceFactory {

    <T extends Request, R> RequestHandler<T, R> getRequestHandler(Class<? extends Request> requestClass);

    List<PipelineBehavior> getPipelineBehaviors();

}
