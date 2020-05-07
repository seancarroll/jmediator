package com.seanthomascarroll.jmediator.micronaut;

import com.seanthomascarroll.jmediator.*;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.inject.BeanDefinition;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ApplicationContextRequestHandlerProvider implements ServiceFactory, ApplicationEventListener<StartupEvent> {

    private ApplicationContext applicationContext;
    private Map<String, Class<RequestHandler>> handlerClassNameToTypeMap = new HashMap<>();

    public ApplicationContextRequestHandlerProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T extends Request, R> RequestHandler<T, R> getRequestHandler(Class<? extends Request> requestClass) {
        Class<RequestHandler> handlerClass = handlerClassNameToTypeMap.get(requestClass.getName());
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
                handlerClassNameToTypeMap.putIfAbsent(requestClass.getName(), beanDefinition.getBeanType());
            } catch (ClassNotFoundException e) {
                throw new NoHandlerForRequestException("request handler not found for class " + beanDefinition.getName(), e);
            }
        }
    }

}
