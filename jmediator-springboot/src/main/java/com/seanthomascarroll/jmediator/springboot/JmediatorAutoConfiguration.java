package com.seanthomascarroll.jmediator.springboot;

import com.seanthomascarroll.jmediator.RequestDispatcher;
import com.seanthomascarroll.jmediator.RequestDispatcherImpl;
import com.seanthomascarroll.jmediator.ServiceFactory;
import com.seanthomascarroll.jmediator.spring.ClasspathScanningServiceFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RequestDispatcher.class)
public class JmediatorAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(RequestDispatcher.class)
    public static class JmediatorConfiguration {

        @Bean
        public RequestDispatcher requestDispatcher(ServiceFactory serviceFactory) {
            return new RequestDispatcherImpl(serviceFactory);
        }

        @Bean
        public ClasspathScanningServiceFactory serviceFactory(ConfigurableListableBeanFactory beanFactory) {
            return new ClasspathScanningServiceFactory(beanFactory);
        }
    }

}
