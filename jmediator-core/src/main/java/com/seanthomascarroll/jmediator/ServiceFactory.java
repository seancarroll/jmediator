package com.seanthomascarroll.jmediator;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;

import java.util.Collection;
import java.util.List;

public interface ServiceFactory {

    <T extends Request, R> RequestHandler<T, R> getRequestHandler(Class<? extends Request> requestClass);

    List<PipelineBehavior> getPipelineBehaviors();

//    <T> T getInstance(Class<T> clazz);
//
//    <T> Collection<T> getInstances(Class<T> clazz);
}
