package com.seanthomascarroll.jmediator.micronaut;

import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import com.seanthomascarroll.jmediator.ServiceFactory;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;

@Factory
public class JmediatorFactory {

    private ServiceFactory serviceFactory;

    public JmediatorFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Singleton
    public RequestDispatcher requestDispatcher() {
        return new RequestDispatcherImpl(serviceFactory);
    }

}
