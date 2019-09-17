# jmediator

[![Build Status](https://travis-ci.org/seancarroll/jmediator.svg?branch=master)](https://travis-ci.org/seancarroll/jmediator)
[![codecov](https://codecov.io/gh/seancarroll/jmediator/branch/master/graph/badge.svg)](https://codecov.io/gh/seancarroll/jmediator)
[![Maintainability](https://api.codeclimate.com/v1/badges/71e99c60f84bf8229d25/maintainability)](https://codeclimate.com/github/seancarroll/jmediator/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/71e99c60f84bf8229d25/test_coverage)](https://codeclimate.com/github/seancarroll/jmediator/test_coverage)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=seancarroll_jmediator&metric=alert_status)](https://sonarcloud.io/dashboard?id=seancarroll_jmediator) 
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=seancarroll_jmediator&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=seancarroll_jmediator)
[![SonarCloud Bugs](https://sonarcloud.io/api/project_badges/measure?project=seancarroll_jmediator&metric=bugs)](https://sonarcloud.io/component_measures/metric/reliability_rating/list?id=seancarroll_jmediator)
[![SonarCloud Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=seancarroll_jmediator&metric=vulnerabilities)](https://sonarcloud.io/component_measures/metric/security_rating/list?id=seancarroll_jmediator)


Simple in-process mediator implementation.

## Prerequisites

Requires Java 8

## Examples

jmediator attempts to decouple the in-process sending of messages from how they are handled.

First, create a message by creating a class that implements the `Request` interface. 
The `Request` interface handles both commands and queries.

```java
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
```

Next, lets create the corresponding handler

```java
@Named
public class HelloRequestHandler implements RequestHandler<HelloRequest, String> {

    @Override
    public String handle(HelloRequest request) {
        return "Hello " + request.getName();
    }

}
```

Finally, we can send a message through the mediator

```java
String response dispatcher.send(new HelloRequest("Sean"));
System.out.println(response); // prints "Hello Sean"
```

## Inspiration

This project was inspired by Jimmy Bogard's [Mediator](https://github.com/jbogard/MediatR) project 
