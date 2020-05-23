package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.RequestHandler;
import io.quarkus.arc.Arc;
import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.arc.runtime.BeanContainerListener;
import io.quarkus.runtime.annotations.Recorder;

import java.util.List;
import java.util.Map;

// BuildExtension.BuildContext
// io.quarkus.arc.processor.BuildExtension.Key
// BEANS
// STEREOTYPES
// Synthetic Beans
//@BuildStep
//BeanRegistrarBuildItem syntheticBean() {
//    return new BeanRegistrarBuildItem(new BeanRegistrar() {
//
//@Override
//public void register(RegistrationContext registrationContext) {
//    registrationContext.configure(String.class).types(String.class).qualifiers(new MyQualifierLiteral()).creator(mc -> mc.returnValue(mc.load("foo"))).done();
//    }
//    }));
//    }
// You can easily filter all class-based beans via the convenient BeanStream returned from the RegistrationContext.beans() method.


@Recorder
public class JmediatorRecorder {

    public void initServiceFactory(BeanContainer container, Map<String, Class<RequestHandler>> handlerClassNames, List<String> behaviorClassNames) {
        JmediatorProducer producer = container.instance(JmediatorProducer.class);
        producer.init(handlerClassNames, behaviorClassNames);
    }

    public BeanContainerListener setHandlerClassNames(Map<String, String> handlerClassNames) {
        return beanContainer -> {
            JmediatorProducer producer = beanContainer.instance(JmediatorProducer.class);
            producer.setHandlerClassNames(handlerClassNames);
        };
    }

    public BeanContainerListener setBehaviors(List<String> behaviorClassNames) {
        return beanContainer -> {
            JmediatorProducer producer = beanContainer.instance(JmediatorProducer.class);
            producer.setBehaviors(behaviorClassNames);
        };
    }

    public void migrate(BeanContainer container) {
//        Liquibase liquibase = container.instance(Liquibase.class);
//        liquibase.update(new Contexts());
    }

    public void setApplicationMigrationFiles(List<String> migrationFiles) {
        // Arc.container()
        // Arc.container().beanManager()
        // QuarkusPathLocationScanner.setApplicationMigrationFiles(migrationFiles);
    }

//    public BeanContainerListener initializeValidatorFactory(Set<Class<?>> classesToBeValidated,
//                                                            Set<String> detectedBuiltinConstraints,
//                                                            boolean hasXmlConfiguration, boolean jpaInClasspath,
//                                                            ShutdownContext shutdownContext, LocalesBuildTimeConfig localesBuildTimeConfig,
//                                                            HibernateValidatorBuildTimeConfig hibernateValidatorBuildTimeConfig) {
//        BeanContainerListener beanContainerListener = new BeanContainerListener() {
//
//        }
//    }
}
