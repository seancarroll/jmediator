package jmediator;

/**
 * Pipeline behavior that surrounds the inner handler.
 * Implementations add additional behavior and call next
 *
 * @param <T> Request type
 */
public interface PipelineBehavior {

	/**
	 * 
	 * @param request
	 * @return
	 */
	<T extends Request, R> R handle(T request, PipelineChain chain);
}
