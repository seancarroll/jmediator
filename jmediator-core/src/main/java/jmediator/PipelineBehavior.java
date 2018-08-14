package jmediator;

/**
 * Pipeline behavior that surrounds the inner handler.
 * Implementations add additional behavior and call next
 *
 * @param <T> Request type
 */
public interface PipelineBehavior<T extends Request, R> {

	/**
	 * 
	 * @param request
	 * @return
	 */
	R handle(T request, PipelineChain chain);
}
