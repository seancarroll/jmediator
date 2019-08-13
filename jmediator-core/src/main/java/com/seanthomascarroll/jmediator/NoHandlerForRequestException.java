package com.seanthomascarroll.jmediator;

/**
 *
 */
public class NoHandlerForRequestException extends RuntimeException {

    private static final long serialVersionUID = -3378587911169482266L;

    public NoHandlerForRequestException(Class<?> type) {
        super("request handler not found for class " + type);
    }

    public NoHandlerForRequestException(String message) {
        super(message);
    }

    public NoHandlerForRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
