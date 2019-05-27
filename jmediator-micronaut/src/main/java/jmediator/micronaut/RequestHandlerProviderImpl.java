package jmediator.micronaut;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.inject.BeanDefinition;
import jmediator.*;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class RequestHandlerProviderImpl implements RequestHandlerProvider, ApplicationEventListener<StartupEvent> {

    private ApplicationContext applicationContext;
    private Map<String, Class<RequestHandler>> handlerClassNameToTypeMap = new HashMap<>();

    public RequestHandlerProviderImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RequestHandler<Request, Object> getRequestHandler(Request request) {
        Class<RequestHandler> handlerClass = handlerClassNameToTypeMap.get(request.getClass().getName());
        return applicationContext.getBean(handlerClass);
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
