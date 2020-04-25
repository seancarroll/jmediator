package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

import com.seanthomascarroll.jmediator.Request;

public class Ping implements Request {
    Ping() {

    }

    Ping(String message) {
        this.message = message;
    }

    String message;

    @Override
    public String toString() {
        return "Ping{" +
            "message='" + message + '\'' +
            '}';
    }
}
