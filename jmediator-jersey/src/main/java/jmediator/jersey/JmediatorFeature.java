package jmediator.jersey;

import io.github.classgraph.ClassGraph;
import jmediator.*;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.InjectionManagerProvider;
import org.glassfish.jersey.internal.inject.InjectionManager;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// https://github.com/tchen319/hairball-j/blob/4a2f87386e578394fee547970d4502618f218421/src/main/java/com/oath/gemini/merchant/cron/QuartzFeature.java
public class JmediatorFeature implements RequestHandlerProvider, Feature {

    private InjectionManager injectionManager;
    private final String[] packagesToScan;
    private Map<Class<?>, Class<RequestHandler>> handlers = new HashMap<>();

    public JmediatorFeature(String... packagesToScan) {
        this.packagesToScan = packagesToScan;
    }


    @Override
    public boolean configure(FeatureContext context) {
        handlers.clear();
        List<String> requestHandlersNames = serviceNames(packagesToScan);
        for (String className : requestHandlersNames) {
            try {
                Class<RequestHandler> clazz = (Class<RequestHandler>) Class.forName(className);
                Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(clazz, RequestHandler.class);
                // we only want to store the class name as the actual handler should be managed by HK2 and could have
                // custom lifecycle or scope depending on how it added to the injection binder
                handlers.putIfAbsent(requestClass, clazz);
            } catch (ClassNotFoundException e) {
                throw new NoHandlerForRequestException("request handler not found for class " + className, e);
            }
        }

        injectionManager = InjectionManagerProvider.getInjectionManager(context);

        // org.glassfish.hk2.utilities.binding.AbstractBinder;
        // Jersey 3675 github issue
        RequestHandlerProvider provider = this;
        context.register(new org.glassfish.jersey.internal.inject.AbstractBinder() {
            @Override
            protected void configure() {
                //RequestDispatcherImpl dispatcher = new RequestDispatcherImpl(new RequestHandlerProviderImpl(injectionManager,"jmediator.sample.jersey"));
                RequestDispatcherImpl dispatcher = new RequestDispatcherImpl(provider);
                bind(dispatcher).to(RequestDispatcher.class);
            }
        });

        return true;
    }

    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request)  {
        Class<RequestHandler> handlerClass =  handlers.get(request.getClass());
        List<RequestHandler<Request, Object>> handlers = injectionManager.getAllInstances(RequestHandler.class);
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
            //.getStandardClasses()
            .getNames();
    }
}
