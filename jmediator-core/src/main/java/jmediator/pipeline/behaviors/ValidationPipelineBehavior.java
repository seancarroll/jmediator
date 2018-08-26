package jmediator.pipeline.behaviors;

import jmediator.Request;
import jmediator.pipeline.PipelineBehavior;
import jmediator.pipeline.PipelineChain;

import javax.validation.*;
import java.util.Set;

/**
 * Pipeline behavior that applies JSR303 bean validation on incoming messages.
 * When validation on a message fails, a ConstraintViolationException is thrown, holding the constraint violations.
 *
 */
public class ValidationPipelineBehavior implements PipelineBehavior {

    private final ValidatorFactory validatorFactory;

    /**
     * Initializes a validation behavior using a default ValidatorFactory
     *
     * @see javax.validation.Validation#buildDefaultValidatorFactory()
     */
    public ValidationPipelineBehavior() {
        this(Validation.buildDefaultValidatorFactory());
    }

    /**
     * Initializes a validation behavior using the given ValidatorFactory.
     *
     * @param validatorFactory the factory providing Validator instances for this pipeline behavior.
     */
    public ValidationPipelineBehavior(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    @Override
    public <T extends Request> Object handle(T request, PipelineChain chain) {
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return chain.doBehavior();
    }
}
