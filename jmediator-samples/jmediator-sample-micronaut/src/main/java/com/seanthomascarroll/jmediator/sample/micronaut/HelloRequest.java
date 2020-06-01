package com.seanthomascarroll.jmediator.sample.micronaut;

import com.seanthomascarroll.jmediator.Request;

public class HelloRequest implements Request {

    private final String name;

    public HelloRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Message{" +
            "payload='" + name + '\'' +
            '}';
    }
}
