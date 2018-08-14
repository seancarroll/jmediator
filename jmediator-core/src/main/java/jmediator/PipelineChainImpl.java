package jmediator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @param <T>
 */
public class PipelineChainImpl<T extends Request, R> implements PipelineChain {

    // TODO: do we want to us an iterator instead?
    private final T request;
    private List<? extends PipelineBehavior<? super T, R>> chain;
    private final RequestHandler<? super T, R> handler;
    private int position = 0;

    /**
     *
     * @param handler
     */
    public PipelineChainImpl(T request, List<? extends PipelineBehavior<? super T, R>> chain, RequestHandler<? super T, R> handler) {
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
