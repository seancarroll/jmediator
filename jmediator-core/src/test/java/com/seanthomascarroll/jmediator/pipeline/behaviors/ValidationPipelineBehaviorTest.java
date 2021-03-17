package com.seanthomascarroll.jmediator.pipeline.behaviors;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class ValidationPipelineBehaviorTest {

    private ValidationPipelineBehavior behavior;
    private PipelineChain pipelineChain;

    @BeforeEach
    void setUp() {
        behavior = new ValidationPipelineBehavior();
        pipelineChain = mock(PipelineChain.class);
    }

    @Test
    void shouldNotThrowExceptionForLegalValues() {
        StubMessage message = new StubMessage("Sean");

        behavior.handle(message, pipelineChain);
        verify(pipelineChain).doBehavior(message);
    }

    @Test
    void shouldThrowConstraintViolationExceptionForIllegalValues() {
        StubMessage message = new StubMessage(null);

        try {
            behavior.handle(message, pipelineChain);
            fail("should throw when there are constraint violations");
        } catch (ConstraintViolationException ex) {
            assertEquals(1, ex.getConstraintViolations().size());
        } catch (Exception ex) {
            fail("wrong exception thrown. ConstraintViolationException should be thrown");
        }
        verify(pipelineChain, never()).doBehavior(message);
    }

    @Test
    void canUseCustomValidatorFactory() {
        StubMessage message = new StubMessage("Sean");
        ValidatorFactory mockValidatorFactory = spy(Validation.buildDefaultValidatorFactory());
        behavior = new ValidationPipelineBehavior(mockValidatorFactory);

        behavior.handle(message, pipelineChain);

        verify(mockValidatorFactory).getValidator();
    }


    private static class StubMessage implements Request {
        @NotNull
        @Size(min = 2)
        private String message;

        StubMessage(String message) {
            this.message = message;
        }
    }

}
