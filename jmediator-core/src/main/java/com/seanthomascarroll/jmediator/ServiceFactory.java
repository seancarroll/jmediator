package com.seanthomascarroll.jmediator;

import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

// TODO: javadocs
public interface ServiceFactory {

    /**
     *
     * @param requestClass
     * @param <T>
     * @param <R>
     * @return
     */
    <T extends Request, R> RequestHandler<T, R> getRequestHandler(Class<? extends Request> requestClass);

    /**
     *
     * @return
     */
    List<PipelineBehavior> getPipelineBehaviors();

    /**
     *
     * @param handles
     */
    default void release(List<Object> handles) {
        // no-op default implementation
    }

    // TODO: better place for this? I think its better than ReflectionsUtils but not sure I love the location
    default Class<?> getRequestClassForHandler(Class<?> clazz) {
        // check that clazz implements RequestHandler interface
        if (!RequestHandler.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("class " + clazz.getCanonicalName() + " must implement " + RequestHandler.class.getSimpleName());
        }

        Class<?> requestType = findRawRequestType(clazz.getGenericInterfaces());
        if (requestType == null) {
            throw new RawTypeForRequestHandlerNotFoundException(clazz);
        }

        return requestType;
    }

    // TODO: make private default when moving to JDK 9+
    static Class<?> findRawRequestType(Type[] types) {
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parametrized = (ParameterizedType) type;
                return findRawRequestType(parametrized.getActualTypeArguments());
            }

            if (type instanceof Class) {
                Class<?> clazz = (Class<?>) type;
                if (Request.class.isAssignableFrom(clazz)) {
                    return clazz;
                }
            }
        }

        return null;
    }
}
