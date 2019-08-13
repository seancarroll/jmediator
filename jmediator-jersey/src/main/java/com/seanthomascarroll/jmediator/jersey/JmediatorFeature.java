package com.seanthomascarroll.jmediator.jersey;

import com.seanthomascarroll.jmediator.*;
import io.github.classgraph.ClassGraph;
import org.glassfish.jersey.InjectionManagerProvider;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.InjectionManager;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: implement autobind or remove
// https://github.com/tchen319/hairball-j/blob/4a2f87386e578394fee547970d4502618f218421/src/main/java/com/oath/gemini/merchant/cron/QuartzFeature.java
public class JmediatorFeature implements RequestHandlerProvider, Feature {

    private final boolean autobind;
    private final String[] packagesToScan;
    private final Map<String, Class<RequestHandler>> handlers = new HashMap<>();
    private InjectionManager injectionManager;

    // TODO: can we support a no-arg constructor?
    // If specific packages are not defined, scanning will occur from the package of the class that declares this annotation
    // how would I get caller class?

    /**
     * @param packagesToScan packages  to look for RequestHandler
     * @see JmediatorFeature#JmediatorFeature(boolean, String...)
     */
    public JmediatorFeature(String... packagesToScan) {
        this(false, packagesToScan);
    }

    /**
     * @param autobind  whether or not RequestHandlers should be registered with DI system
     * @param packagesToScan  packages to look for RequestHandler
     */
    public JmediatorFeature(boolean autobind, String... packagesToScan) {
        this.autobind = autobind;
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

        RequestHandlerProvider provider = this;

        // https://github.com/eclipse-ee4j/jersey/issues/3675
        // This currently doesn't work...org.glassfish.hk2.utilities.binding.AbstractBinder
        context.register(new AbstractBinder() {
            @Override
            protected void configure() {
                RequestDispatcherImpl dispatcher = new RequestDispatcherImpl(provider);
                bind(dispatcher).to(RequestDispatcher.class);

                for (Class clazz : handlers.values()) {
                    bind(clazz).to(clazz);
                }
            }
        });

        return true;
    }

    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request) {
        Class<RequestHandler> handlerClass = handlers.get(request.getClass().getName());
        RequestHandler<Request, Object> handler = injectionManager.getInstance(handlerClass);
        if (handler == null) {
            throw new NoHandlerForRequestException("request handler not found for class " + request.getClass());
        }
        return handler;
    }

    private static List<String> getRequestHandlerClassNames(String... packages) {
        return new ClassGraph().whitelistPackages(packages)
            .scan()
            .getClassesImplementing(RequestHandler.class.getName())
            .getNames();
    }
}
