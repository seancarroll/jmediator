package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.ServiceFactory;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class JmediatorProducer {

    private volatile QuarkusServiceFactory serviceFactory;

    @Produces
    @Dependent
    @Default
    public ServiceFactory producesServiceFactory() {
        return serviceFactory;
    }

    @Produces
    @Dependent
    @Default
    public RequestDispatcher producesRequestDispatcher(ServiceFactory serviceFactory) {
        return new RequestDispatcherImpl(serviceFactory);
    }

    public void init(Map<String, Class<RequestHandler>> handlerClassNames, List<Class<PipelineBehavior>> behaviorClassNames) {
        serviceFactory = new QuarkusServiceFactory(handlerClassNames, behaviorClassNames);
    }
}
