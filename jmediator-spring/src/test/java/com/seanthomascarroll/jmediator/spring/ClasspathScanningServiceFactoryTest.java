package com.seanthomascarroll.jmediator.spring;

import com.seanthomascarroll.jmediator.NoHandlerForRequestException;
import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.ServiceFactoryException;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    void shouldThrowWhenRequestHandlerBeanCannotBeInstantiated() {
        ConfigurableListableBeanFactory mockBeanFactory = mock(ConfigurableListableBeanFactory.class, RETURNS_DEEP_STUBS);
        when(mockBeanFactory.getBeanNamesForType(RequestHandler.class)).thenReturn(new String[]{PingHandler.class.getName()});

        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        when(beanDefinition.getBeanClassName()).thenReturn(PingHandler.class.getName());
        when(mockBeanFactory.getBeanDefinition(PingHandler.class.getName())).thenReturn(beanDefinition);
        when(mockBeanFactory.getBean(PingHandler.class.getName(), RequestHandler.class)).thenThrow(FatalBeanException.class);

        ClasspathScanningServiceFactory serviceFactory = new ClasspathScanningServiceFactory(mockBeanFactory);
        serviceFactory.postProcessBeanFactory(mockBeanFactory);

        assertThrows(NoHandlerForRequestException.class, () -> serviceFactory.getRequestHandler(Ping.class));
    }

    @Test
    void shouldRegisterPipelineBehavior() {
        ClasspathScanningServiceFactory serviceFactory = new ClasspathScanningServiceFactory(beanFactory);

        List<PipelineBehavior> behaviors = serviceFactory.getPipelineBehaviors();

        assertEquals(1, behaviors.size());
        assertTrue(behaviors.get(0) instanceof NoopPipelineBehavior);
    }

    @Test
    void shouldThrowWhenPipelineBehaviorBeanCannotBeInstantiated() {
        ConfigurableListableBeanFactory mockBeanFactory = mock(ConfigurableListableBeanFactory.class);
        when(mockBeanFactory.getBeansOfType(PipelineBehavior.class)).thenThrow(FatalBeanException.class);

        ClasspathScanningServiceFactory serviceFactory = new ClasspathScanningServiceFactory(mockBeanFactory);

        assertThrows(ServiceFactoryException.class, serviceFactory::getPipelineBehaviors);
    }

}
