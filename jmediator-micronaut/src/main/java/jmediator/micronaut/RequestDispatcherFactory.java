package jmediator.micronaut;

import io.micronaut.context.annotation.Factory;
import jmediator.RequestDispatcher;
import jmediator.RequestDispatcherImpl;
import jmediator.RequestHandlerProvider;

import javax.inject.Singleton;

@Factory
public class RequestDispatcherFactory {

    private RequestDispatcher dispatcher;
    private RequestHandlerProvider provider;

    public RequestDispatcherFactory(RequestHandlerProvider provider) {
        this.provider = provider;
    }

    @Singleton
    public RequestDispatcher requestDispatcher() {
        RequestDispatcherImpl requestDispatcher = new RequestDispatcherImpl(provider);
        return requestDispatcher;
    }

}
