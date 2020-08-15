package com.seanthomascarroll.jmediator;

/**
 *
 */
public class RawTypeForRequestHandlerNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 468779694752462401L;

    public RawTypeForRequestHandlerNotFoundException(Class<?> clazz) {
        super("raw type not found for " + clazz.getCanonicalName());
    }
}
