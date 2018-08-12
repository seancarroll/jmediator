package jmediator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @param <T>
 * @param <R>
 */
public class PipelineChainImpl<T extends Request, R> implements PipelineChain<T, R> {

    // TODO: do we want to us an iterator instead?
    private List<PipelineBehavior<T, R>> chain = new ArrayList<>();
    private final RequestHandler<T, R> handler;
    private int position = 0;

    /**
     *
     * @param handler
     */
    public PipelineChainImpl(RequestHandler<T, R> handler) {
        this.handler = handler;
    }

    // TODO: this probably doesn't below here
    /**
     *
     * @param behavior
     */
    public void addBehavior(final PipelineBehavior<T, R> behavior) {
        chain.add(behavior);
    }

    @Override
    public R doBehavior(T request) {
        if (position < chain.size()) {
            return chain.get(position++).handle(request, this);
        } else {
            return handler.handle(request);
        }
    }

}
