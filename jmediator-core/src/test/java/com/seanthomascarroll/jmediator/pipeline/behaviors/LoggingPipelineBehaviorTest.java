package com.seanthomascarroll.jmediator.pipeline.behaviors;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class LoggingPipelineBehaviorTest {

    private LoggingPipelineBehavior behavior;
    private PipelineChain pipelineChain;
    private ListAppender appender;

    @BeforeEach
    public void setUp() {
        behavior = new LoggingPipelineBehavior();
        pipelineChain = mock(PipelineChain.class);
    }

    @AfterEach
    @BeforeAll
    public void cleanUp() {
        if (appender != null) {
            appender.clear();
        }
    }

    @Test
    public void canLogNullReturnValue() {
        when(pipelineChain.doBehavior()).thenReturn(null);

        behavior.handle(new Ping(), pipelineChain);

        appender = (ListAppender) LoggerContext.getContext(false).getLogger("com.seanthomascarroll.jmediator.pipeline.behaviors").getAppenders().get("List");
        assertEquals(2, appender.getMessages().size());
        assertTrue(appender.getMessages().get(1).contains("null"));
    }

    @Test
    void canLogCustomReturnValue() {

        when(pipelineChain.doBehavior()).thenReturn(new Pong("World"));

        Ping request = new Ping("Hello");
        behavior.handle(request, pipelineChain);

        appender = (ListAppender) LoggerContext.getContext(false).getLogger("com.seanthomascarroll.jmediator.pipeline.behaviors").getAppenders().get("List");
        assertEquals(2, appender.getMessages().size());
        // TODO: use hamcrest or assertj...probably assertj
        assertTrue(appender.getMessages().get(0).contains("Hello"));
        assertTrue(appender.getMessages().get(1).contains("World"));
    }

    @Test
    void shouldNotLogReturnWhenExceptionOccurs() {

        when(pipelineChain.doBehavior()).thenThrow(new RuntimeException());

        try {
            Ping request = new Ping();

            behavior.handle(request, pipelineChain);
            fail("exception should propagate to caller");
        } catch (Exception ex) {
            // expected
        }

        appender = (ListAppender) LoggerContext.getContext(false).getLogger("com.seanthomascarroll.jmediator.pipeline.behaviors").getAppenders().get("List");
        assertEquals(1, appender.getMessages().size());
        assertTrue(appender.getMessages().get(0).contains("Ping"));
    }

    @Test
    void canUseCustomLogger() {
        when(pipelineChain.doBehavior()).thenReturn(new Pong("World"));
        String loggerName = "com.seanthomascarroll.jmediator.pipeline.logging";

        Ping request = new Ping("Hello");
        LoggingPipelineBehavior behaviorWithCustomLogger = new LoggingPipelineBehavior(loggerName);

        behaviorWithCustomLogger.handle(request, pipelineChain);

        appender = (ListAppender) LoggerContext.getContext(false).getLogger(loggerName).getAppenders().get("List");
        assertEquals(2, appender.getMessages().size());
        // TODO: use assertj
        assertTrue(appender.getMessages().get(0).contains("Hello"));
        assertTrue(appender.getMessages().get(1).contains("World"));
    }

    private static class Ping implements Request {
        Ping() {

        }

        Ping(String message) {
            this.message = message;
        }

        String message;

        @Override
        public String toString() {
            return "Ping{" +
                "message='" + message + '\'' +
                '}';
        }
    }

    private static class Pong {
        Pong(String message) {
            this.message = message;
        }

        String message;

        @Override
        public String toString() {
            return "Pong{" +
                "message='" + message + '\'' +
                '}';
        }
    }

}
