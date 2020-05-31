package com.seanthomascarroll.jmediator.spring;

import com.seanthomascarroll.jmediator.RequestHandler;

public class NotBeanHandler implements RequestHandler<NotBean, Void> {

    @Override
    public Void handle(NotBean request) {
        return null;
    }
}
