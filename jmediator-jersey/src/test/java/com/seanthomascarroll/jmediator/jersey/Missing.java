package com.seanthomascarroll.jmediator.jersey;

import com.seanthomascarroll.jmediator.Request;

public class Missing implements Request {

    private String field;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
