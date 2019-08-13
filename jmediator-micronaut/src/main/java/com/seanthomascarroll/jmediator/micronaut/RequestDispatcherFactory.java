package com.seanthomascarroll.jmediator.micronaut;

import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import com.seanthomascarroll.jmediator.RequestHandlerProvider;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;

@Factory
public class RequestDispatcherFactory {

    private RequestHandlerProvider provider;

    public RequestDispatcherFactory(RequestHandlerProvider provider) {
        this.provider = provider;
    }

    @Singleton
    public RequestDispatcher requestDispatcher() {
        return new RequestDispatcherImpl(provider);
    }

}
