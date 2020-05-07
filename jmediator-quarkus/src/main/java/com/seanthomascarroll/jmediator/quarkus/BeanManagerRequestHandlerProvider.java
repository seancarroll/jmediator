package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.*;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO: I would like to avoid this and manually register
// TODO: I'm not positive this is the appropriate way to handle this in Quarkus.
// Quarkus has an interesting extension model that splits work between deployment aka build time and runtime.
// Could I do this work as part of deployment time?
@ApplicationScoped
public class BeanManagerRequestHandlerProvider implements ServiceFactory {

    private static final TypeLiteral<RequestHandler<?, ?>> REQUEST_HANDLER_TYPE_LITERAL = new TypeLiteral<RequestHandler<?, ?>>() { };

    private final BeanManager beanManager;
    private final Map<String, Class<?>> handlerClassNames = new HashMap<>();

    @Inject
    public BeanManagerRequestHandlerProvider(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public <T extends Request, R> RequestHandler<T, R> getRequestHandler(Class<? extends Request> requestClass) {
        Class<?> handlerClassName = handlerClassNames.get(requestClass.getName());
        if (handlerClassName == null) {
            throw new NoHandlerForRequestException(requestClass);
        }

        Set<Bean<?>> beans = beanManager.getBeans(handlerClassName);
        if (beans == null || beans.size() != 1) {
            throw new NoHandlerForRequestException(requestClass);
        }

        Bean<?> bean = beans.iterator().next();
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        return (RequestHandler<T, R>) beanManager.getReference(bean, RequestHandler.class, creationalContext);
    }

    @Override
    public List<PipelineBehavior> getPipelineBehaviors() {
        Set<Bean<?>> beans = beanManager.getBeans(PipelineBehavior.class);
        List<PipelineBehavior> behaviors = new ArrayList<>(beans.size());
        while (beans.iterator().hasNext()) {
            Bean<?> bean = beans.iterator().next();
            CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
            behaviors.add((PipelineBehavior) beanManager.getReference(bean, PipelineBehavior.class, creationalContext));
        }
        return behaviors;
    }

    void onStart(@Observes StartupEvent ev) {
        handlerClassNames.clear();
        Set<Bean<?>> requestHandlersBeans = beanManager.getBeans(REQUEST_HANDLER_TYPE_LITERAL.getType());
        for (Bean<?> bean : requestHandlersBeans) {
            Class<?> requestClass = ReflectionUtils.getTypeArgumentForGenericInterface(bean.getBeanClass(), RequestHandler.class);
            // we only want to store the class name as the actual handler should be managed by Quarkus and could have
            // custom lifecycle or scope depending on how it added to the injection binder
            handlerClassNames.putIfAbsent(requestClass.getName(), bean.getBeanClass());
        }
    }

}
