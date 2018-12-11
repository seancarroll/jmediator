package jmediator.spring;

import jmediator.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashMap;
import java.util.Map;

// TODO: is there a way to do other scopes outside of singleton which is essentially is because you always
// get back the same requesthandler.
// TODO: Update name - the name is terrible. This is really a ComponentScanning request handler provider or something like that
// TODO: can we support Spring component scanning and manual registration that will adhere to scope?
// TODO: can we support registering by class name?
// TODO: allow users to validate that all request classes have associated handlers registered. Take a look at Marten
// are there other projects that have a similar validation idea? Mediatr?
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
 *
 * Default implementation that uses BeanFactory to find all registered RequestHandler beans.
 *
 * Is also an ApplicationListener that hooks into the ContextRefreshedEvent to find all registered RequestHandlers
 * to create a map between Request classes and RequestHandler class names
 *
 */
public class RequestHandlerProviderImpl implements RequestHandlerProvider, ApplicationListener<ContextRefreshedEvent> {

    private ConfigurableListableBeanFactory beanFactory;
    private Map<Class<?>, String> handlerClassNames = new HashMap<>();

    public RequestHandlerProviderImpl(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request) {
        String handlerClassName = handlerClassNames.get(request.getClass());
        RequestHandler<Request, Object> handler = beanFactory.getBean(handlerClassName, RequestHandler.class);
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
        handlerClassNames.clear();
        String[] requestHandlersNames = beanFactory.getBeanNamesForType(RequestHandler.class);
        for (String beanName : requestHandlersNames) {
            try {
                BeanDefinition requestHandler = beanFactory.getBeanDefinition(beanName);
                Class<?> handlerClass = Class.forName(requestHandler.getBeanClassName());
                Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handlerClass, RequestHandler.class);
                // we only want to store the class name as the actual handler should be managed by Spring and could have
                // custom lifecycle or scope depending on how it added to the injection binder
                handlerClassNames.putIfAbsent(requestClass, beanName);
            } catch (ClassNotFoundException e) {
                throw new NoHandlerForRequestException("request handler not found for class " + beanName, e);
            }
        }
    }
}
