package jmediator;

/**
 * Defines a mediator aka command bus to encapsulate request/response interaction pattern.
 * The mechanism that dispatches request messages to their appropriate request handler.
 * Only a single handler may be subscribed for a single type of request at any time.
 */
public interface RequestDispatcher {

    /**
     * Send a response to handler
     * 
     * @param request
     * @return
     */
    <R> R send(Request request);

}
