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
	<T extends Request> Object handle(T request, PipelineChain chain);
}
