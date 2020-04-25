package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

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
