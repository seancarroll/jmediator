package jmediator;

/**
 * 
 *
 */
public interface RequestHandlerProvider {

    /**
     * 
     * @param request
     * @return
     */
    RequestHandler<?, ?> getRequestHandler(Request request);

}
