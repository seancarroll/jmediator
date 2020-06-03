package com.seanthomascarroll.jmediator.pipeline.honeycomb;

public class Pong {
    Pong(String message) {
        this.message = message;
    }

    String message;

    @Override
    public String toString() {
        return "Pong{" +
            "message='" + message + '\'' +
            '}';
    }
}
