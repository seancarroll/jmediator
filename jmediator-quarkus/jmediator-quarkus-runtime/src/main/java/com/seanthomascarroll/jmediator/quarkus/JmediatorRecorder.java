package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.annotations.Recorder;

import java.util.List;
import java.util.Map;

@Recorder
public class JmediatorRecorder {

    public void initServiceFactory(BeanContainer container,
                                   Map<String, Class<RequestHandler>> handlerClassNames,
                                   List<Class<PipelineBehavior>> behaviorClassNames) {
        JmediatorProducer producer = container.instance(JmediatorProducer.class);
        producer.init(handlerClassNames, behaviorClassNames);
    }

}
