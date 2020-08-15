package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.ServiceFactory;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import com.seanthomascarroll.jmediator.pipeline.behaviors.LoggingPipelineBehavior;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JmediatorProcessPipelineBehaviorTest {

    @Inject
    ServiceFactory serviceFactory;

    @RegisterExtension
    static final QuarkusUnitTest CONFIG = new QuarkusUnitTest()
        .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
        .addClass(JmediatorBehaviorFactory.class));

    @Test
    void shouldRegisterPipelineBehaviors() {
        assertEquals(2, serviceFactory.getPipelineBehaviors().size());
    }

    static class NoopBehavior implements PipelineBehavior {

        @Override
        public Object handle(Request request, PipelineChain chain) {
            return chain.doBehavior(request);
        }
    }

    class JmediatorBehaviorFactory {

        @Produces
        public LoggingPipelineBehavior nullBehavior() {
            return new LoggingPipelineBehavior();
        }

    }
}
