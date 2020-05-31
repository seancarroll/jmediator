package com.seanthomascarroll.jmediator.micronaut;

import com.seanthomascarroll.jmediator.NoHandlerForRequestException;
import com.seanthomascarroll.jmediator.ReflectionUtils;
import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.ServiceFactory;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.inject.BeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Singleton
public class ApplicationContextRequestHandlerProvider implements ServiceFactory, ApplicationEventListener<StartupEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextRequestHandlerProvider.class);

    private final ApplicationContext applicationContext;
    private final Map<String, Class<RequestHandler>> handlerClassNameToTypeMap = new HashMap<>();

    public ApplicationContextRequestHandlerProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Request, R> RequestHandler<T, R> getRequestHandler(Class<? extends Request> requestClass) {
        Class<RequestHandler> handlerClass = handlerClassNameToTypeMap.get(requestClass.getName());
        if (handlerClass == null) {
            throw new NoHandlerForRequestException(requestClass.getName());
        }
        return applicationContext.getBean(handlerClass);
    }

    @Override
    public List<PipelineBehavior> getPipelineBehaviors() {
        return new ArrayList<>(applicationContext.getBeansOfType(PipelineBehavior.class));
    }

    @Override
    public void onApplicationEvent(StartupEvent event) {
        handlerClassNameToTypeMap.clear();
        Collection<BeanDefinition<RequestHandler>> beanDefinitions = applicationContext.getBeanDefinitions(RequestHandler.class);
        for (BeanDefinition<RequestHandler> beanDefinition : beanDefinitions) {
            try {
                Class<?> handlerClass = Class.forName(beanDefinition.getName());
                Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(handlerClass, RequestHandler.class);
                // we only want to store the bean type as the actual handler should be managed by Micronaut and could have
                // custom lifecycle or scope depending on how its registered
                Class<?> previous = handlerClassNameToTypeMap.putIfAbsent(requestClass.getName(), beanDefinition.getBeanType());
                if (previous != null) {
                    LOGGER.warn("{} already associated with {}", requestClass.getName(), beanDefinition.getName());
                }
            } catch (ClassNotFoundException e) {
                throw new NoHandlerForRequestException("request handler not found for class " + beanDefinition.getName(), e);
            }
        }
    }

}
