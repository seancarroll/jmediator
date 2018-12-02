package jmediator.spring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jmediator.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

// TODO: is there a way to do other scopes outside of singleton which is essentially is because you always
// get back the same requesthandler.
// TODO: Update name - the name is terrible. This is really a ComponentScanning request handler provider
// or something like that
// TODO: can we support Spring component scanning and manual registration that will adhere to scope?
// TODO: can we support registering by class name?
// Will AutowireCapableBeanFactory help?
// maybe we dont keep a cache and just always call the beanfactory?
// what does spring do under the covers?
// https://github.com/spring-projects/spring-framework/blob/a89e716cc71a8741385a0224b5d7eb7ce009e11a/spring-web/src/main/java/org/springframework/web/context/support/WebApplicationContextUtils.java
// AbstractAutowireCapableBeanFactory
// AbstractBeanFactory
/**
 * Default implementation that looks into the application event listener context
 * refreshed event and finds all beans implements RequestHandler
 *
 */
public class RequestHandlerProviderImpl implements RequestHandlerProvider, ApplicationListener<ContextRefreshedEvent> {

    private ConfigurableListableBeanFactory beanFactory;
    private Map<Class<?>, RequestHandler<Request, Object>> handlers = new HashMap<>();
    private Map<Class<?>, String> h = new HashMap<>();

    public RequestHandlerProviderImpl(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request) {
        RequestHandler<Request, Object> handler = handlers.get(request.getClass());
        if (handler == null) {
            throw new NoHandlerForRequestException("request handler not found for class " + request.getClass());
        }
        return handler;


//        // if we dont cache the actual handler
//        String beanName = h.get(request.getClass());
//        // TODO: do we need to do a null check? If so can we do it here and would that guarantee handler would not be null?
//        RequestHandler<Request, Object> handler = beanFactory.getBean(beanName, RequestHandler.class);
//        if (handler == null) {
//            throw new NoHandlerForRequestException("request handler not found for class " + request.getClass());
//        }
//        return handler;
    }

    /**
     *
     * @param event
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        handlers.clear();
        String[] requestHandlersNames = beanFactory.getBeanNamesForType(RequestHandler.class);
        for (String beanName : requestHandlersNames) {
            try {
                BeanDefinition requestHandler = beanFactory.getBeanDefinition(beanName);
                Class<?> handlerClass = Class.forName(requestHandler.getBeanClassName());
                Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handlerClass, RequestHandler.class);
                RequestHandler<Request, Object> handler = beanFactory.getBean(beanName, RequestHandler.class);
                handlers.putIfAbsent(requestClass, handler);

                // If we dont cache the actual handler
                // would we still need the catch and throw here?
//                BeanDefinition requestHandler = beanFactory.getBeanDefinition(beanName);
//                Class<?> handlerClass = Class.forName(requestHandler.getBeanClassName());
//                Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handlerClass, RequestHandler.class);
//                h.putIfAbsent(requestClass, beanName);

            } catch (ClassNotFoundException e) {
                throw new NoHandlerForRequestException("request handler not found for class " + beanName, e);
            }
        }
    }

    Map<Class<?>, RequestHandler<Request, Object>> getHandlers() {
        return Collections.unmodifiableMap(handlers);
    }
}
