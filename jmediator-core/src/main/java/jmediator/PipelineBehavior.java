package jmediator;

/**
 * Pipeline behavior that surrounds the inner handler.
 * Implementations add additional behavior and call next
 *
 * @param <T> Request type
 * @param <R> Response type
 */
public interface PipelineBehavior<T extends Request, R> /*extends RequestHandler<T, R>*/ {

	/**
	 * 
	 * @param request
	 * @return
	 */
	R handle(T request, PipelineChain<T, R> chain);
}
