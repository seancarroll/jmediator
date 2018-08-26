package jmediator.pipeline.behaviors;

import jmediator.Request;
import jmediator.pipeline.PipelineBehavior;
import jmediator.pipeline.PipelineChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pipeline behavior that logs incoming messages and their result to a SLF4J logger.
 * Allows configuration of the name under which the logger should log the statements.
 * <p/>
 * Incoming messages and successful executions are logged at the {@code INFO} level.
 */
public class LoggingPipelineBehavior implements PipelineBehavior {

    private final Logger logger;

    /**
     * Initialize the LoggingPipelineBehavior with the given {@code loggerName}.
     * The actual logging implementation will use this name to decide the appropriate log level and location.
     * See the documentation of your logging implementation for more information.
     *
     * @param loggerName the name of the logger
     */
    public LoggingPipelineBehavior(String loggerName) {
        this(LoggerFactory.getLogger(loggerName));
    }

    /**
     * Initialize the LoggingPipelineBehavior with the default logger name, which is the fully qualified class name of this
     * logger.
     *
     * @see #LoggingPipelineBehavior(String)
     */
    public LoggingPipelineBehavior() {
        this(LoggerFactory.getLogger(LoggingPipelineBehavior.class));
    }

    LoggingPipelineBehavior(Logger logger) {
        this.logger = logger;
    }

    @Override
    public <T extends Request> Object handle(T request, PipelineChain chain) {
        logger.info("incoming request {}", request);
        Object response = chain.doBehavior();
        logger.info("response {}", response);
        return response;
    }

}
