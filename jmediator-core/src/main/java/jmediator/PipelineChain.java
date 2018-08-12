package jmediator;

/**
 *
 *
 */
public interface PipelineChain<T extends Request, R> {

    /**
     *
     * @param request
     * @return
     */
    R doBehavior(T request);

}
