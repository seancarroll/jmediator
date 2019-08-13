package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.Request;

public class PingRequest implements Request {

    private String value;

    public PingRequest() {
    }

    public PingRequest(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
