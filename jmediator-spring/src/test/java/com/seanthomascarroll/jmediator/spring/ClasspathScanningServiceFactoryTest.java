package com.seanthomascarroll.jmediator.spring;

import com.seanthomascarroll.jmediator.NoHandlerForRequestException;
import com.seanthomascarroll.jmediator.RequestHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class ClasspathScanningServiceFactoryTest {

    @Inject
    private ConfigurableListableBeanFactory beanFactory;

    @Inject
    private PingHandler pingHandler;

    @Test
    void shouldRegisterRequestHandlers() {
        ClasspathScanningServiceFactory serviceFactory = new ClasspathScanningServiceFactory(beanFactory);

        serviceFactory.postProcessBeanFactory(beanFactory);

        Map<String, String> handlerNames = serviceFactory.getHandlerClassNames();
        assertEquals(1, handlerNames.size());
        assertEquals("pingHandler", handlerNames.values().iterator().next());
    }

    @Test
    void shouldSuccessfullyGetRegisteredRequestHandlerByRequestClassName() {
        ClasspathScanningServiceFactory serviceFactory = new ClasspathScanningServiceFactory(beanFactory);
        serviceFactory.postProcessBeanFactory(beanFactory);

        RequestHandler<Ping, String> handler = serviceFactory.getRequestHandler(Ping.class);

        assertEquals(pingHandler, handler);
    }

    @Test
    void shouldThrowForRequestClassThatHasNoRegisteredHandler() {
        ClasspathScanningServiceFactory serviceFactory = new ClasspathScanningServiceFactory(beanFactory);
        serviceFactory.postProcessBeanFactory(beanFactory);

        assertThrows(NoHandlerForRequestException.class, () -> serviceFactory.getRequestHandler(MissingHandler.class));
    }

    @Test
    void shouldThrowForRequestClassWhoseHandlerIsNotARegisteredBean() {
        ClasspathScanningServiceFactory serviceFactory = new ClasspathScanningServiceFactory(beanFactory);
        serviceFactory.postProcessBeanFactory(beanFactory);

        assertThrows(NoHandlerForRequestException.class, () -> serviceFactory.getRequestHandler(NotBean.class));
    }
}
