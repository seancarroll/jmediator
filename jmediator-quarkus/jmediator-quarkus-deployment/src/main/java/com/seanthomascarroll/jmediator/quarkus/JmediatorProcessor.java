package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.runtime.annotations.Recorder;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;
import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 *
 */
public class JmediatorProcessor {

    //private static final Logger LOGGER = LoggerFactory.getLogger(JmediatorProcessor.class);
    private static final Logger LOGGER = Logger.getLogger(JmediatorProcessor.class);

    @BuildStep
    FeatureBuildItem feature() {
        // Describes a functionality provided by an extension. The info is displayed to users.
        return new FeatureBuildItem("jmediator");
    }

//    @BuildStep
//    void unremoveableBeans(BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
//        // Convenient factory method to create an unremovable build item for a single bean class.
//        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(JmediatorProducer.class));
//        // additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusServiceFactory.class));
//        // additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(RequestDispatcherImpl.class));
//    }


//    @BuildStep
//    public JmediatorHandlerBuildItem handlers(BeanArchiveIndexBuildItem beanArchiveIndex) {
//        Map<String, String> handlerClassNames = new HashMap<>();
//        IndexView indexView = beanArchiveIndex.getIndex();
//        Collection<ClassInfo> handlers = indexView.getAllKnownImplementors(DotName.createSimple(RequestHandler.class.getName()));
//        for (ClassInfo handler : handlers) {
//            try {
//                String handlerName = handler.name().toString();
//                Class<?> handlerClass = Class.forName(handlerName);
//                // TODO: get appropriate interface instead of just the first
//                Class<?> requestClass = handler.interfaceTypes().get(0).asParameterizedType().arguments().get(0).getClass();
//
//                handlerClassNames.put(requestClass.getName(), handlerName);
//                // handlerProducer.produce(new JmediatorHandlerBuildItem(handlerClass));
//                LOGGER.infof("Configured bean: %s", handlerClass);
//            } catch (ClassNotFoundException e) {
//                LOGGER.warn("Failed to load bean class", e);
//            } catch (Exception ex) {
//                LOGGER.errorf("failed to get requestClass for %s", handler.name().toString());
//            }
//        }
//
//        return new JmediatorHandlerBuildItem(handlerClassNames);
//    }

//    @BuildStep
//    public JmediatorPipelineBuildItem behaviors(BeanArchiveIndexBuildItem beanArchiveIndex) {
//
//        List<String> behaviorClassNames = new ArrayList<>();
//        IndexView indexView = beanArchiveIndex.getIndex();
//        Collection<ClassInfo> pipelineBehaviors = indexView.getAllKnownImplementors(DotName.createSimple(PipelineBehavior.class.getName()));
//        for (ClassInfo behavior : pipelineBehaviors) {
//            String beanClass = behavior.name().toString();
//            behaviorClassNames.add(beanClass);
//        }
//
//        return new JmediatorPipelineBuildItem(behaviorClassNames);
//    }

    @BuildStep
    void scan(CombinedIndexBuildItem beanArchiveIndex,
              BuildProducer<AdditionalBeanBuildItem> additionalBeans,
              BuildProducer<JmediatorHandlerBuildItem> handlerProducer,
              BuildProducer<JmediatorPipelineBuildItem> behaviorProducer) {

        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(JmediatorProducer.class));

        Map<String, Class<RequestHandler>> handlerClassNames = new HashMap<>();
        IndexView indexView = beanArchiveIndex.getIndex();
        Collection<ClassInfo> handlers = indexView.getAllKnownImplementors(DotName.createSimple(RequestHandler.class.getName()));
        for (ClassInfo handler : handlers) {
            try {
                Class<RequestHandler> handlerClass = (Class<RequestHandler>) Class.forName(handler.name().toString());
                // Class<?> handlerClass =  Class.forName(handler.name().toString());
                // TODO: get appropriate interface instead of just the first
                LOGGER.infof("found parameterized type args %s", handler.interfaceTypes().get(0).asParameterizedType().arguments().get(0));
                String requestClass = handler.interfaceTypes().get(0).asParameterizedType().arguments().get(0).toString();

                LOGGER.infof("request class [%s] found for handler [%s]", requestClass, handlerClass);

                handlerClassNames.put(requestClass, handlerClass);
                additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(handlerClass));
                // handlerProducer.produce(new JmediatorHandlerBuildItem(handlerClass));
                LOGGER.infof("Configured bean: %s", handlerClass.getName());
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Failed to load bean class", e);
            } catch (Exception ex) {
                LOGGER.errorf("failed to get requestClass for %s", handler.name().toString());
            }
        }

        handlerProducer.produce(new JmediatorHandlerBuildItem(handlerClassNames));
//        recorder.setHandlerClassNames(handlerClassNames);

        List<String> behaviorClassNames = new ArrayList<>();
        Collection<ClassInfo> pipelineBehaviors = indexView.getAllKnownImplementors(DotName.createSimple(PipelineBehavior.class.getName()));
        for (ClassInfo behavior : pipelineBehaviors) {
            String beanClass = behavior.name().toString();
            behaviorClassNames.add(beanClass);
            additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(beanClass));
//            try {
//                Class<?> beanClass = Class.forName(behavior.name().toString());
//                behaviorProducer.produce(new JmediatorPipelineBuildItem(beanClass));
//                LOGGER.info("Configured bean: {}", beanClass);
//            } catch (ClassNotFoundException e) {
//                LOGGER.warn("Failed to load bean class", e);
//            }
        }
        behaviorProducer.produce(new JmediatorPipelineBuildItem(behaviorClassNames));
    }

//    @BuildStep
//    void unremoveableBeans(BuildProducer<AdditionalBeanBuildItem> additionalBeans,
//                           JmediatorHandlerBuildItem handlers,
//                           JmediatorPipelineBuildItem behaviors) {
//        // Convenient factory method to create an unremovable build item for a single bean class.
//        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(JmediatorProducer.class));
//        // additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(QuarkusServiceFactory.class));
//        // additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(RequestDispatcherImpl.class));
//
//        if (handlers.getHandlerClassNames() != null) {
//            for (Class<RequestHandler> requestHandlerClass : handlers.getHandlerClassNames().values()) {
//                additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(requestHandlerClass));
//            }
//        }
//
//        if (behaviors.getBehaviorClassNames() != null) {
//            for (String pipelineBehaviorClass : behaviors.getBehaviorClassNames()) {
//                additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(pipelineBehaviorClass));
//            }
//        }
//    }


    @BuildStep
    @Record(RUNTIME_INIT)
    void configure(JmediatorRecorder recorder,
                   BeanContainerBuildItem beanContainer,
                   JmediatorHandlerBuildItem handlers,
                   JmediatorPipelineBuildItem behaviors) {
        recorder.initServiceFactory(beanContainer.getValue(), handlers.getHandlerClassNames(), behaviors.getBehaviorClassNames());
    }

//    @BuildStep
//    @Record(STATIC_INIT)
//    void scanForBeans(BeanContainerBuildItem beanContainer,
//                      JmediatorRecorder recorder,
//                      BeanArchiveIndexBuildItem beanArchiveIndex,
//                      // BuildProducer<UnremovableBeanBuildItem> unremovableProducer,
//                      BuildProducer<JmediatorHandlerBuildItem> handlerProducer,
//                      BuildProducer<JmediatorPipelineBuildItem> behaviorProducer) {
//
//        Map<String, Class<RequestHandler>> handlerClassNames = new HashMap<>();
//        IndexView indexView = beanArchiveIndex.getIndex();
//        Collection<ClassInfo> handlers = indexView.getAllKnownImplementors(DotName.createSimple(RequestHandler.class.getName()));
//        for (ClassInfo handler : handlers) {
//            try {
//                Class<RequestHandler> handlerClass = (Class<RequestHandler>) Class.forName(handler.name().toString());
//                // Class<?> handlerClass =  Class.forName(handler.name().toString());
//                // TODO: get appropriate interface instead of just the first
//                LOGGER.infof("found parameterized type args %s", handler.interfaceTypes().get(0).asParameterizedType().arguments().get(0));
//                String requestClass = handler.interfaceTypes().get(0).asParameterizedType().arguments().get(0).toString();
//
//                LOGGER.infof("request class [%s] found for handler [%s]", requestClass, handlerClass);
//
//                handlerClassNames.put(requestClass, handlerClass);
//                // handlerProducer.produce(new JmediatorHandlerBuildItem(handlerClass));
//                LOGGER.infof("Configured bean: %s", handlerClass.getName());
//            } catch (ClassNotFoundException e) {
//                LOGGER.warn("Failed to load bean class", e);
//            } catch (Exception ex) {
//                LOGGER.errorf("failed to get requestClass for %s", handler.name().toString());
//            }
//        }
//
////        handlerProducer.produce(new JmediatorHandlerBuildItem(handlerClassNames));
////        recorder.setHandlerClassNames(handlerClassNames);
//
//        List<String> behaviorClassNames = new ArrayList<>();
//        Collection<ClassInfo> pipelineBehaviors = indexView.getAllKnownImplementors(DotName.createSimple(PipelineBehavior.class.getName()));
//        for (ClassInfo behavior : pipelineBehaviors) {
//            String beanClass = behavior.name().toString();
//            behaviorClassNames.add(beanClass);
////            try {
////                Class<?> beanClass = Class.forName(behavior.name().toString());
////                behaviorProducer.produce(new JmediatorPipelineBuildItem(beanClass));
////                LOGGER.info("Configured bean: {}", beanClass);
////            } catch (ClassNotFoundException e) {
////                LOGGER.warn("Failed to load bean class", e);
////            }
//        }
//
////        behaviorProducer.produce(new JmediatorPipelineBuildItem(behaviorClassNames));
////        recorder.setBehaviors(behaviorClassNames);
//
//        recorder.initServiceFactory(beanContainer.getValue(), handlerClassNames, behaviorClassNames);
//    }

//    @BuildStep
//    @Record(RUNTIME_INIT)
//    public void configure(JmediatorRecorder recorder,
//                   JmediatorHandlerBuildItem handlerBuildItem,
//                   JmediatorPipelineBuildItem behaviorBuildItem) {
//
//        recorder.setHandlerClassNames(handlerBuildItem.getHandlerClassNames());
//        recorder.setBehaviors(behaviorBuildItem.getBehaviorClassNames());
//    }


//    @Record(ExecutionTime.STATIC_INIT)
//    @BuildStep
//    void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer,
//               BuildProducer<FeatureBuildItem> featureProducer,
//               JmediatorRecorder recorder,
//               BuildProducer<BeanContainerListenerBuildItem> containerListenerProducer,
//               DataSourceInitializedBuildItem dataSourceInitializedBuildItem) {
//
//        featureProducer.produce(new FeatureBuildItem("liquibase"));
//
//        AdditionalBeanBuildItem beanBuilItem = AdditionalBeanBuildItem.unremovableOf(LiquibaseProducer.class);
//        additionalBeanProducer.produce(beanBuilItem);
//
//        containerListenerProducer.produce(
//            new BeanContainerListenerBuildItem(recorder.setLiquibaseConfig(liquibaseConfig)));
//    }



//     TODO: need to build ServiceFactory
//     pass private final Map<String, Class<?>> handlerClassNames = new HashMap<>(); map as build item?
//     along with BeanArchiveIndexBuildItem so that servicefactory and get bean?
//     maybe use BeanContainerBuildItem or BeanContainer from below?


    // TestProcessor#configureBeans
//    @BuildStep
//    @Record(RUNTIME_INIT)
//    void configureBeans(TestRecorder recorder, List<TestBeanBuildItem> testBeans,
//    BeanContainerBuildItem beanContainer,
//    TestRunTimeConfig runTimeConfig) {
//
//        for (TestBeanBuildItem testBeanBuildItem : testBeans) {
//            Class<IConfigConsumer> beanClass = testBeanBuildItem.getConfigConsumer();
//            recorder.configureBeans(beanContainer.getValue(), beanClass, buildAndRunTimeConfig, runTimeConfig); (3)
//        }
//    }

    // TestRecorder#configureBeans
//    public void configureBeans(BeanContainer beanContainer, Class<IConfigConsumer> beanClass,
//                               TestBuildAndRunTimeConfig buildTimeConfig,
//                               TestRunTimeConfig runTimeConfig) {
//        log.infof("Begin BeanContainerListener callback\n");
//        IConfigConsumer instance = beanContainer.instance(beanClass); (4)
//        instance.loadConfig(buildTimeConfig, runTimeConfig); (5)
//        log.infof("configureBeans, instance=%s\n", instance);
//    }

//     https://www.baeldung.com/quarkus-extension-java
//     BeanContainerListenerBuildItem
//     https://github.com/eugenp/tutorials/tree/master/quarkus-extension
//
//    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//    ResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(classLoader);
}
