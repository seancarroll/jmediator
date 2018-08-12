package jmediator;

/**
 * Interceptors that allow requests to be intercepted before they are dispatched
 * by the dispatcher.
 * <p>
 * I've thought about having this return IRequest<?> request so that
 * preRequestHandlers could alter the request message eg Adding currentUser to
 * request.
 *
 */
// TODO: deprecate remove this in favor on pipeline chain
public interface IPreRequestHandler {

    /**
     * Invoked each time a request is about to be handled by the dispatcher
     * @param request The request message to be dispatched to the dispatcher
     */
    void handle(Request request);
}
