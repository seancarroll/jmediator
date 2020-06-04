package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.ServiceFactory;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class JmediatorProducer {

    private volatile QuarkusServiceFactory serviceFactory;

    @Produces
    @Singleton
    public ServiceFactory producesServiceFactory() {
        return serviceFactory;
    }

    @Produces
    @Singleton
    public RequestDispatcher producesRequestDispatcher(ServiceFactory serviceFactory) {
        return new RequestDispatcherImpl(serviceFactory);
    }

    void init(Map<String, Class<? extends RequestHandler<?, ?>>> handlerClassNames, List<Class<? extends PipelineBehavior>> behaviorClassNames) {
        serviceFactory = new QuarkusServiceFactory(handlerClassNames, behaviorClassNames);
    }
}
