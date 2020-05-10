package com.seanthomascarroll.jmediator.jersey;

import com.seanthomascarroll.jmediator.Request;

public class HelloRequest implements Request {

    private String name;

    public HelloRequest() {}

    public HelloRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
