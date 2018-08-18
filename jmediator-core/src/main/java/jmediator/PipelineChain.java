package jmediator;

/**
 *
 *
 */
public interface PipelineChain {

    /**
     *
     * @return
     */
    <R> R doBehavior();

}
