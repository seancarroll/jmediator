package com.seanthomascarroll.jmediator.spring;

import com.seanthomascarroll.jmediator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
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
 * RequestHandlerProvider that hooks into the context's underlying bean factory via Spring's {@BeanFactoryPostProcessor}.
 * <p>
 * Get's all beans for type {@RequestHandler} registered within the application context and registers them with
 * Jmediator which keeps a internal map of request class name to handler bean name.
 *
 */
public class ComponentScanningRequestHandlerProvider implements RequestHandlerProvider, BeanFactoryPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentScanningRequestHandlerProvider.class);

    private ConfigurableListableBeanFactory beanFactory;
    private Map<String, String> handlerClassNames = new HashMap<>();

    public ComponentScanningRequestHandlerProvider(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request) {
        String handlerClassName = handlerClassNames.get(request.getClass().getName());
        if (handlerClassName == null) {
            throw new NoHandlerForRequestException("request handler bean not registered for class " + request.getClass());
        }

        try {
            return beanFactory.getBean(handlerClassName, RequestHandler.class);
        } catch (BeansException ex) {
            throw new NoHandlerForRequestException("request handler not found for class " + request.getClass());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        String[] requestHandlersNames = configurableListableBeanFactory.getBeanNamesForType(RequestHandler.class);
        for (String beanName : requestHandlersNames) {
            LOGGER.debug("registering requesthandler {} with jmediator", beanName);
            try {
                BeanDefinition requestHandler = configurableListableBeanFactory.getBeanDefinition(beanName);
                Class<?> handlerClass = Class.forName(requestHandler.getBeanClassName());
                Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handlerClass, RequestHandler.class);
                // we only want to store the class name as the actual handler should be managed by Spring and could have
                // custom lifecycle or scope depending on how it added to the injection binder
                String previous = handlerClassNames.putIfAbsent(requestClass.getName(), beanName);
                if (previous != null) {
                    LOGGER.warn("{} already associated with {}", requestClass.getName(), beanName);
                }

            } catch (ClassNotFoundException e) {
                throw new NoHandlerForRequestException("request handler not found for class " + beanName, e);
            }
        }
    }
}
