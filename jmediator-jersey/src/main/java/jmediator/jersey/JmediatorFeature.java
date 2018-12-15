package jmediator.jersey;

import io.github.classgraph.ClassGraph;
import jmediator.*;
import org.glassfish.jersey.InjectionManagerProvider;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.ServiceHolder;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// https://github.com/tchen319/hairball-j/blob/4a2f87386e578394fee547970d4502618f218421/src/main/java/com/oath/gemini/merchant/cron/QuartzFeature.java
public class JmediatorFeature implements RequestHandlerProvider, Feature {

    private final boolean autobind;
    private final String[] packagesToScan;
    private InjectionManager injectionManager;
    private Map<String, Class<RequestHandler>> handlers = new HashMap<>();

    /**
     *
     * @param autobind whether or not RequestHandlers should be registered with DI system
     * @param packagesToScan packages to look for RequestHandler
     */
    public JmediatorFeature(boolean autobind, String... packagesToScan) {
        this.autobind = autobind;
        this.packagesToScan = packagesToScan;
    }

    @Override
    public boolean configure(FeatureContext context) {
        handlers.clear();
        List<String> requestHandlersNames = serviceNames(packagesToScan);
        List<Class> classesToBind = new ArrayList<>(autobind ? requestHandlersNames.size() : 0);

        injectionManager = InjectionManagerProvider.getInjectionManager(context);
//        for (ServiceHolder<RequestHandler> serviceHolder = injectionManager.getAllServiceHolders(RequestHandler.class)) {
//
//        }

        for (String className : requestHandlersNames) {
            try {
                Class clazz = Class.forName(className);
                Class requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(clazz, RequestHandler.class);
                // we only want to store the class name as the actual handler should be managed by Jersey's injection
                // framework and could have custom lifecycle or scope depending on how it added to the injection binder
                handlers.putIfAbsent(requestClass.getName(), clazz);

                if (autobind) {
                    classesToBind.add(clazz);
                }
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
                //RequestDispatcherImpl dispatcher = new RequestDispatcherImpl(new RequestHandlerProviderImpl(injectionManager,"jmediator.sample.jersey"));
                RequestDispatcherImpl dispatcher = new RequestDispatcherImpl(provider);
                bind(dispatcher).to(RequestDispatcher.class);
            }
        });

        // TODO: do we even need to do this? what if we just relied on inhibitor maven plugin?
        if (!classesToBind.isEmpty()) {
            context.register(new AbstractBinder() {
                @Override
                protected void configure() {
                    // TOOD: do we need to include scope annotation?
                    for (Class clazz : classesToBind) {
                        bind(clazz).to(clazz);
                    }
                }
            });
        }

        return true;
    }

    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request)  {
        Class<RequestHandler> handlerClass =  handlers.get(request.getClass());
        RequestHandler<Request, Object> handler = injectionManager.getInstance(handlerClass);
        if (handler == null) {
            throw new NoHandlerForRequestException("request handler not found for class " + request.getClass());
        }
        return handler;
    }

    private static List<String> serviceNames(String... packages) {
        return new ClassGraph().whitelistPackages(packages)
            .scan()
            .getClassesImplementing(RequestHandler.class.getName())
            .getNames();
    }
}
