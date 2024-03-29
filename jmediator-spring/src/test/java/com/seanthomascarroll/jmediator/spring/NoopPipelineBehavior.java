package com.seanthomascarroll.jmediator.spring;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import jakarta.inject.Named;

@Named
public class NoopPipelineBehavior implements PipelineBehavior {
    @Override
    public Object handle(Request request, PipelineChain chain) {
        return chain.doBehavior(request);
    }
}
