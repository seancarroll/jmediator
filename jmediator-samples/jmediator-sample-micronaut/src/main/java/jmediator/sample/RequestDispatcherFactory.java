package jmediator.sample;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import jmediator.RequestDispatcher;
import jmediator.RequestDispatcherImpl;
import jmediator.RequestHandlerProvider;
import jmediator.micronaut.RequestHandlerProviderImpl;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Factory
public class RequestDispatcherFactory {

    private RequestDispatcher dispatcher;
    private ApplicationContext applicationContext;
    private RequestHandlerProvider provider;

    @Inject
    public RequestDispatcherFactory(RequestHandlerProvider provider, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.provider = provider;
    }

//    @PostConstruct
//    void initialize() {
//        return new RequestHandlerProviderImpl();
//    }

    @Singleton
    public RequestDispatcher requestDispatcher() {

        RequestDispatcherImpl requestDispatcher = new RequestDispatcherImpl(provider);
        return requestDispatcher;
    }

}
