package com.seanthomascarroll.jmediator;

/**
 *
 */
public interface RequestHandlerProvider {

    /**
     * @param request
     * @return
     */
    <T extends Request, R> RequestHandler<T, R> getRequestHandler(T request);

}
