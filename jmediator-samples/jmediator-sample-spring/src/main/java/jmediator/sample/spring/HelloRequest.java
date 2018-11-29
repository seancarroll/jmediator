package jmediator.sample.spring;

import jmediator.Request;

public class HelloRequest implements Request {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Message{" +
            "payload='" + name + '\'' +
            '}';
    }
}
