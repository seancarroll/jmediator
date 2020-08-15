package com.seanthomascarroll.jmediator.spring;

import com.seanthomascarroll.jmediator.NoHandlerForRequestException;
import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.ServiceFactory;
import com.seanthomascarroll.jmediator.ServiceFactoryException;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClasspathScanningServiceFactory implements ServiceFactory, BeanFactoryPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathScanningServiceFactory.class);

    private final ConfigurableListableBeanFactory beanFactory;
    private final Map<String, String> handlerClassNames = new HashMap<>();

    public ClasspathScanningServiceFactory(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Request, R> RequestHandler<T, R> getRequestHandler(Class<? extends Request> requestClass) {
        String handlerClassName = handlerClassNames.get(requestClass.getName());
        if (handlerClassName == null) {
            throw new NoHandlerForRequestException("request handler bean not registered for class " + requestClass);
        }

        try {
            return beanFactory.getBean(handlerClassName, RequestHandler.class);
        } catch (BeansException ex) {
            throw new NoHandlerForRequestException(requestClass);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        String[] requestHandlersNames = configurableListableBeanFactory.getBeanNamesForType(RequestHandler.class);
        for (String beanName : requestHandlersNames) {
            LOGGER.debug("registering request handler {} with jmediator", beanName);
            try {
                BeanDefinition requestHandler = configurableListableBeanFactory.getBeanDefinition(beanName);
                Class<?> handlerClass = Class.forName(requestHandler.getBeanClassName());
                Class<?> requestClass = getRequestClassForHandler(handlerClass);

                // we only want to store the class name as the actual handler should be managed by Spring and could have
                // custom lifecycle or scope depending on how it added to the injection binder
                String previous = handlerClassNames.putIfAbsent(requestClass.getName(), beanName);
                if (previous != null) {
                    LOGGER.warn("{} already associated with {}", requestClass.getName(), beanName);
                }

            } catch (ClassNotFoundException e) {
                throw new NoHandlerForRequestException(beanName, e);
            }
        }
    }

    @Override
    public List<PipelineBehavior> getPipelineBehaviors() {
        try {
            return new ArrayList<>(beanFactory.getBeansOfType(PipelineBehavior.class).values());
        } catch (BeansException ex) {
            throw new ServiceFactoryException("Failed to create PipelineBehavior bean(s)", ex);
        }
    }

    // TODO: add @VisibleForTest annotation
    Map<String, String> getHandlerClassNames() {
        return handlerClassNames;
    }
}
