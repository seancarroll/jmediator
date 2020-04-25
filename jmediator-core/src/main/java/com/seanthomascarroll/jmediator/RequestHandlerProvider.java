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

    <T extends Request> void register(RequestHandler<? super T, ?> handler);
}
