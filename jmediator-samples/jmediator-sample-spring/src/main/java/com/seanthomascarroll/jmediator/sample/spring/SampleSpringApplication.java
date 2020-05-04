package com.seanthomascarroll.jmediator.sample.spring;

import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import com.seanthomascarroll.jmediator.RequestHandlerProvider;
import com.seanthomascarroll.jmediator.spring.ComponentScanningRequestHandlerProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SampleSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleSpringApplication.class, args);
    }

    @Bean
    public RequestDispatcher requestDispatcher(RequestHandlerProvider requestHandlerProvider) {
        return new RequestDispatcherImpl(requestHandlerProvider);
    }

    @Bean
    public ComponentScanningRequestHandlerProvider requestHandlerProvider(ConfigurableListableBeanFactory beanFactory) {
        return new ComponentScanningRequestHandlerProvider(beanFactory);
    }

}
