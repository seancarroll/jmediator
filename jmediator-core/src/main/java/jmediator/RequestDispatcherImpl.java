package jmediator;

import java.util.ArrayList;
import java.util.Collections;
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

    private Iterable<? extends IPreRequestHandler> preRequestHandlers = Collections.emptyList();
    private Iterable<? extends IPostRequestHandler> postRequestHandlers = Collections.emptyList();


    /**
     *
     * @param requestHandlerProvider
     */
    public RequestDispatcherImpl(RequestHandlerProvider requestHandlerProvider) {
        this.requestHandlerProvider = requestHandlerProvider;
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T send(Request request) {

        for (IPreRequestHandler preRequestHandler : preRequestHandlers) {
            preRequestHandler.handle(request);
        }
        
        RequestHandler<Request, ?> handler = requestHandlerProvider.getRequestHandler(request);
        T response = (T) handler.handle(request);

        for (IPostRequestHandler postRequestHandler : postRequestHandlers) {
            postRequestHandler.handle(request, response);
        }
        
        return response;
    }
    

    /**
     * Registers the given list of pre-interceptors to the dispatcher. All
     * incoming requests will pass through the interceptors at the given order
     * before the request is passed to the handler for processing.
     *
     * @param preRequestHandlers
     *            the interceptors to invoke prior to request being handled
     */
    public void setPreRequestHandlers(List<? extends IPreRequestHandler> preRequestHandlers) {
        this.preRequestHandlers = new ArrayList<>(preRequestHandlers);
    }

    /**
     * Registers the given list of post-interceptors to the dispatcher. All
     * incoming requests will pass through the interceptors at the given order
     * after the request is handled by the dispatcher
     *
     * @param postRequestHandlers
     *            The interceptors to invoke after the request has been handled
     */
    public void setPostRequestHandlers(List<? extends IPostRequestHandler> postRequestHandlers) {
        this.postRequestHandlers = new ArrayList<>(postRequestHandlers);
    }

}
