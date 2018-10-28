package jmediator;

/**
 * Defines a handler for a request
 * @param <T> The type of message to be handled
 * @param <R> The type of response to be returned by the handler
 */
@FunctionalInterface
public interface RequestHandler<T extends Request, R> {

    /**
     * Handles a request
     * 
     * @param request message to be handled
     * @return Response from the handler
     */
    R handle(T request);
}
