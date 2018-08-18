package jmediator;

import java.util.List;

/**
 *
 * @param <T>
 */
public class PipelineChainImpl<T extends Request, R> implements PipelineChain {

    private final T request;
    private List<? extends PipelineBehavior> chain;
    private final RequestHandler<? super T, R> handler;
    private int position = 0;

    /**
     *
     * @param handler
     */
    public PipelineChainImpl(T request, List<? extends PipelineBehavior> chain, RequestHandler<? super T, R> handler) {
        this.request = request;
        this.chain = chain;
        this.handler = handler;
    }

	//@SuppressWarnings("unchecked")
	@Override
	public R doBehavior() {
        if (position < chain.size()) {
            return chain.get(position++).handle(request, this);
        } else {
            return handler.handle(request);
        }
	}


}
