package com.seanthomascarroll.jmediator;

/**
 *
 */
public class RawTypeForGenericInterfaceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 468779694752462401L;

    public RawTypeForGenericInterfaceNotFoundException(Class<?> clazz) {
        super("raw type for generic interface not found for " + clazz.getCanonicalName());
    }
}
