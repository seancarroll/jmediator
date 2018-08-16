package jmediator;

import java.util.List;

/**
 *
 * @param <T>
 */
public class PipelineChainImpl<T extends Request> implements PipelineChain {

    // TODO: do we want to us an iterator instead?
    private final T request;
    private List<? extends PipelineBehavior> chain;
    private final RequestHandler<? super T, ?> handler;
    private int position = 0;

    /**
     *
     * @param handler
     */
    public PipelineChainImpl(T request, List<? extends PipelineBehavior> chain, RequestHandler<? super T, ?> handler) {
        this.request = request;
        this.chain = chain;
        this.handler = handler;
    }


    @Override
    public Object doBehavior() {
        if (position < chain.size()) {
            return chain.get(position++).handle(request, this);
        } else {
            return handler.handle(request);
        }
    }

}
