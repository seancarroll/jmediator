package jmediator;


/**
 * Interceptors that allow requests to be intercepted after they are dispatched by the dispatcher.
 *
 */
// TODO: deprecate remove this in favor on pipeline chain
public interface IPostRequestHandler {

    /**
     * 
     * @param request
     * @param response
     */
    void handle(Request request, Object response);

}
