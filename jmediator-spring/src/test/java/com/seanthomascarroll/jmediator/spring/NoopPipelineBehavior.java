package com.seanthomascarroll.jmediator.spring;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;

import javax.inject.Named;

@Named
public class NoopPipelineBehavior implements PipelineBehavior {
    @Override
    public <T extends Request> Object handle(T request, PipelineChain<T> chain) {
        return chain.doBehavior(request);
    }
}
