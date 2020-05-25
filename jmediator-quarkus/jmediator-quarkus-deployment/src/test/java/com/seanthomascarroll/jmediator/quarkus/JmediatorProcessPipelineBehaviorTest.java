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

public class JmediatorProcessPipelineBehaviorTest {

    @Inject
    ServiceFactory serviceFactory;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
        .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
        .addClass(JmediatorBehaviorFactory.class));

    @Test
    void shouldRegisterPipelineBehaviors() {
        assertEquals(2, serviceFactory.getPipelineBehaviors().size());
    }

    static class NoopBehavior implements PipelineBehavior {

        @Override
        public <T extends Request> Object handle(T request, PipelineChain chain) {
            return chain.doBehavior();
        }
    }

    public class JmediatorBehaviorFactory {

        @Produces
        public LoggingPipelineBehavior nullBehavior() {
            return new LoggingPipelineBehavior();
        }
        
    }
}