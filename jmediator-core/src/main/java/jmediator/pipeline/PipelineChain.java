package jmediator.pipeline;

/**
 * The pipeline chain manages the flow of a message through a chain of behaviors and ultimately to the request handler.
 * Behaviors may continue processing via this chain by calling the {@link #doBehavior()} method.
 * Alternatively, they can block processing by returning without calling either of these methods.
 *
 */
public interface PipelineChain {

    /**
     *
     * @return
     */
    Object doBehavior();

}
