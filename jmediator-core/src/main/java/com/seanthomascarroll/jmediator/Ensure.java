package com.seanthomascarroll.jmediator;

public final class Ensure {

    private Ensure() {
        // static methods only
    }

    public static <T> T notNull(T argument) {
        if (argument == null) {
            throw new IllegalArgumentException("cannot be null");
        }
        return argument;
    }

}
