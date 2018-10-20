package jmediator.pipeline.behaviors;

import jmediator.Request;
import jmediator.pipeline.PipelineChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class ValidationPipelineBehaviorTest {

    private ValidationPipelineBehavior behavior;
    private PipelineChain pipelineChain;

    @BeforeEach
    public void setUp() {
        behavior = new ValidationPipelineBehavior();
        pipelineChain = mock(PipelineChain.class);
    }

    @Test
    public void shouldNotThrowExceptionForLegalValues() {
        StubMessage message = new StubMessage("Sean");

        behavior.handle(message, pipelineChain);
        verify(pipelineChain).doBehavior();
    }

    @Test
    public void shouldThrowConstraintViolationExceptionForIllegalValues() {
        StubMessage message = new StubMessage(null);

        try {
            behavior.handle(message, pipelineChain);
            fail("should throw when there are constraint violations");
        } catch (ConstraintViolationException ex) {
            assertEquals(1, ex.getConstraintViolations().size());
        } catch (Exception ex) {
            fail("wrong exception thrown. ConstraintViolationException should be thrown");
        }
        verify(pipelineChain, never()).doBehavior();
    }

    @Test
    public void canUseCustomValidatorFactory() {
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

        public StubMessage(String message) {
            this.message = message;
        }
    }

}