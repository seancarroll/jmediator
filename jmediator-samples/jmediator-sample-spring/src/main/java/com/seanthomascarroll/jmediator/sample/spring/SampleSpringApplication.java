package com.seanthomascarroll.jmediator.sample.spring;

import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import com.seanthomascarroll.jmediator.RequestHandlerProvider;
import com.seanthomascarroll.jmediator.spring.RequestHandlerProviderImpl;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.inject.Inject;

@SpringBootApplication
public class SampleSpringApplication {

    @Inject
    private ConfigurableListableBeanFactory beanFactory;

    public static void main(String[] args) {
        SpringApplication.run(SampleSpringApplication.class, args);
    }

    @Bean
    public RequestDispatcher requestDispatcher() {
        RequestDispatcherImpl requestDispatcher = new RequestDispatcherImpl(requestHandlerProvider());
        return requestDispatcher;
    }

    @Bean
    public RequestHandlerProvider requestHandlerProvider() {
        return new RequestHandlerProviderImpl(beanFactory);
    }

}
