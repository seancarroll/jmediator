package jmediator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Default implementation that looks into the application event listener context
 * refreshed event and finds all beans implements RequestHandler
 *
 */
public class RequestHandlerProviderImpl implements RequestHandlerProvider, ApplicationListener<ContextRefreshedEvent> {

    private ConfigurableListableBeanFactory beanFactory;
    private Map<Class<?>, RequestHandler<Request, Object>> handlers = new HashMap<>();

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
            } catch (ClassNotFoundException e) {
                throw new NoHandlerForRequestException("request handler not found for class " + beanName, e);
            }
        }
    }

    Map<Class<?>, RequestHandler<Request, Object>> getHandlers() {
        return Collections.unmodifiableMap(handlers);
    }
}
