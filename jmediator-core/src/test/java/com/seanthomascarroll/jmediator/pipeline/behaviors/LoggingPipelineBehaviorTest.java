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


class LoggingPipelineBehaviorTest {
    private LoggingPipelineBehavior behavior;
    private PipelineChain pipelineChain;

    @BeforeEach
    void setUp() {
        behavior = new LoggingPipelineBehavior();
        pipelineChain = mock(PipelineChain.class);
    }

    @BeforeAll
    static void init() {
        clearAppender();
    }

    @AfterEach
    void cleanUp() {
        clearAppender();
    }

    private static void clearAppender() {
        ListAppender appender = getListAppender("com.seanthomascarroll.jmediator.pipeline.behaviors");
        if (appender != null) {
            appender.clear();
        }
    }

    @Test
    void canLogNullReturnValue() {
        Ping request = new Ping();
        when(pipelineChain.doBehavior(request)).thenReturn(null);

        behavior.handle(request, pipelineChain);

        ListAppender appender = getListAppender("com.seanthomascarroll.jmediator.pipeline.behaviors");
        assertEquals(2, appender.getMessages().size());
        assertTrue(appender.getMessages().get(1).contains("null"));
    }

    @Test
    void canLogCustomReturnValue() {

        Ping request = new Ping("Hello");

        when(pipelineChain.doBehavior(request)).thenReturn(new Pong("World"));

        behavior.handle(request, pipelineChain);

        ListAppender appender = getListAppender("com.seanthomascarroll.jmediator.pipeline.behaviors");

        assertEquals(2, appender.getMessages().size());
        // TODO: use hamcrest or assertj...probably assertj
        assertTrue(appender.getMessages().get(0).contains("Hello"));
        assertTrue(appender.getMessages().get(1).contains("World"));
    }

    @Test
    void shouldNotLogReturnWhenExceptionOccurs() {
        Ping request = new Ping();
        when(pipelineChain.doBehavior(request)).thenThrow(new RuntimeException());

        try {
            behavior.handle(request, pipelineChain);
            fail("exception should propagate to caller");
        } catch (Exception ex) {
            // expected
        }

        ListAppender appender = getListAppender("com.seanthomascarroll.jmediator.pipeline.behaviors");
        assertEquals(1, appender.getMessages().size());
        assertTrue(appender.getMessages().get(0).contains("Ping"));
    }

    @Test
    void canUseCustomLogger() {
        Ping request = new Ping("Hello");
        when(pipelineChain.doBehavior(request)).thenReturn(new Pong("World"));
        String loggerName = "com.seanthomascarroll.jmediator.pipeline.logging";

        LoggingPipelineBehavior behaviorWithCustomLogger = new LoggingPipelineBehavior(loggerName);

        behaviorWithCustomLogger.handle(request, pipelineChain);

        ListAppender appender = getListAppender(loggerName);
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

    private static ListAppender getListAppender(String name) {
        return (ListAppender) LoggerContext.getContext(false)
            .getLogger(name)
            .getAppenders()
            .get("List");
    }

}
