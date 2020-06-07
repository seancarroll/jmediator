package com.seanthomascarroll.jmediator.jersey;

import com.seanthomascarroll.jmediator.*;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import io.github.classgraph.ClassGraph;
import org.glassfish.jersey.InjectionManagerProvider;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.InjectionManager;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class JmediatorFeature implements ServiceFactory, Feature {

    private final String[] packagesToScan;
    private final Map<String, Class<RequestHandler>> handlers = new HashMap<>();
    private InjectionManager injectionManager;

    /**
     * @param packagesToScan packages  to look for RequestHandler
     */
    public JmediatorFeature(String... packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override
    public boolean configure(FeatureContext context) {
        // I've attempted to get services through injectionManager.getAllInstances and injectionManager.getAllServiceHolders
        // but both of those never return any instances. I've also attempted to implement ContainerLifecycleListener
        // and get services through injection manager via onStartUp and onRestart but no luck there either
        // so I'm falling back to using classGraph
        List<String> requestHandlersNames = getRequestHandlerClassNames(packagesToScan);
        injectionManager = InjectionManagerProvider.getInjectionManager(context);
        for (String className : requestHandlersNames) {
            try {
                Class clazz = Class.forName(className);
                Class requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(clazz, RequestHandler.class);
                // we only want to store the class name as the actual handler should be managed by Jersey's injection
                // framework and could have custom lifecycle or scope depending on how it added to the injection binder
                handlers.putIfAbsent(requestClass.getName(), clazz);
            } catch (ClassNotFoundException e) {
                throw new NoHandlerForRequestException("request handler not found for class " + className, e);
            }
        }

        ServiceFactory serviceFactory = this;

        // https://github.com/eclipse-ee4j/jersey/issues/3675
        // This currently doesn't work...org.glassfish.hk2.utilities.binding.AbstractBinder
        context.register(new AbstractBinder() {
            @Override
            protected void configure() {
                RequestDispatcherImpl dispatcher = new RequestDispatcherImpl(serviceFactory);
                bind(dispatcher).to(RequestDispatcher.class);

                for (Class<?> clazz : handlers.values()) {
                    bind(clazz).to(clazz);
                }
            }
        });

        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Request, R> RequestHandler<T, R> getRequestHandler(Class<? extends Request> requestClass) {
        Class<RequestHandler> handlerClass = handlers.get(requestClass.getName());
        if (handlerClass == null) {
            throw new NoHandlerForRequestException("request handler bean not registered for class " + requestClass);
        }

        RequestHandler<T, R> handler = injectionManager.getInstance(handlerClass);
        if (handler == null) {
            throw new NoHandlerForRequestException(requestClass);
        }
        return handler;
    }

    @Override
    public List<PipelineBehavior> getPipelineBehaviors() {
        return injectionManager.getAllInstances(PipelineBehavior.class);
    }

    private static List<String> getRequestHandlerClassNames(String... packages) {
        return new ClassGraph().whitelistPackages(packages)
            .scan()
            .getClassesImplementing(RequestHandler.class.getName())
            .getNames();
    }
}
