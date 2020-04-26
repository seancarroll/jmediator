package com.seanthomascarroll.jmediator.pipeline.behaviors;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;

import javax.validation.*;
import java.util.Set;

/**
 * Pipeline behavior that applies JSR303 bean validation on incoming messages.
 * When validation on a message fails, a ConstraintViolationException is thrown, holding the constraint violations.
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

        // TODO: check if JSR303 implementation (aka hibernate validator) is on the classpath.
        // log a warning if not but still continue
        // might make sense to move this into a validation package
        // name it HibernateValidatorPipelineBehavior
        // add JFluentValidationPipelineBehavior as well

        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return chain.doBehavior();
    }
}
