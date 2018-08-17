package jmediator;

import java.util.List;

/**
 * Implementation of the dispatcher (aka command bus) that dispatches requests
 * to the handlers subscribed to the specific type of request. Interceptors may
 * be configured to add processing to requests regardless of their type
 *
 * could use isAssignableFrom to determine if pipeline behavior should be executed
 * Look to see how spring does application event as it should be very similar in that
 * you register application event listeners/handlers that can handle certain types
 * and spring routes them appropriately
 * ApplicationListener
 * https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/event/EventListener.html
 * https://github.com/spring-projects/spring-framework/blob/efce7902c40e55d907baaa10b2dc071322e7be11/spring-context/src/main/java/org/springframework/context/event/GenericApplicationListenerAdapter.java
 */
// TODO: Axon SimpleCommandBus has a subscribe method to register handlers for requests.
// I should add that as the default with the others way to register via class scanning, etc.
// That should likely into a default RequestHandlerProvider
public class RequestDispatcherImpl implements RequestDispatcher {

    private final RequestHandlerProvider requestHandlerProvider;

    // TODO: add register/subscribe method
    private final List<PipelineBehavior> handlerInterceptors;

    /**
     *
     * @param requestHandlerProvider
     */
    public RequestDispatcherImpl(RequestHandlerProvider requestHandlerProvider, List<PipelineBehavior> handlerInterceptors) {
        this.requestHandlerProvider = requestHandlerProvider;
        this.handlerInterceptors = handlerInterceptors;
    }

    /**
     *
     */
    @Override
    public <R> R send(Request request) {
        return doSend(request);
    }

    @SuppressWarnings({ "unchecked"})
	private <T extends Request, R> R doSend(T request) {
        RequestHandler<? super T, ?> handler = (RequestHandler<? super T, ?>) requestHandlerProvider.getRequestHandler(request);

        PipelineChain chain = new PipelineChainImpl<T>(request, handlerInterceptors, handler);

        return (R) chain.doBehavior();
    }


}
