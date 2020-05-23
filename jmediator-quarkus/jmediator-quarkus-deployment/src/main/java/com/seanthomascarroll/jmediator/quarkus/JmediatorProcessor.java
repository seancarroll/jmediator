package com.seanthomascarroll.jmediator.quarkus;

import com.seanthomascarroll.jmediator.RequestHandler;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Type;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

/**
 *
 */
public class JmediatorProcessor {

    private static final Logger LOGGER = Logger.getLogger(JmediatorProcessor.class);

    private static final DotName PIPELINE_BEHAVIOR_DOT_NAME = DotName.createSimple(PipelineBehavior.class.getName());
    private static final DotName REQUEST_HANDLER_DOT_NAME = DotName.createSimple(RequestHandler.class.getName());


    @BuildStep
    FeatureBuildItem feature() {
        // Describes a functionality provided by an extension. The info is displayed to users.
        return new FeatureBuildItem("jmediator");
    }

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
                @SuppressWarnings("unchecked")
                Class<RequestHandler> handlerClass = (Class<RequestHandler>) Class.forName(handler.name().toString());
                String requestClass = getRequestClass(handler);
                if (requestClass == null) {
                    continue;
                }
                handlerClassNames.put(requestClass, handlerClass);
                additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(handlerClass));
                LOGGER.debugf("Configured bean: %s", handlerClass.getName());
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Failed to load bean class", e);
            } catch (Exception ex) {
                LOGGER.errorf("failed to get requestClass for %s", handler.name().toString());
            }
        }

        handlerProducer.produce(new JmediatorHandlerBuildItem(handlerClassNames));

        List<Class<PipelineBehavior>> behaviorClassNames = new ArrayList<>();
        Collection<ClassInfo> pipelineBehaviors = indexView.getAllKnownImplementors(PIPELINE_BEHAVIOR_DOT_NAME);
        for (ClassInfo behavior : pipelineBehaviors) {
            try {
                Class<PipelineBehavior> behaviorClass = (Class<PipelineBehavior>) Class.forName(behavior.name().toString());
                behaviorClassNames.add(behaviorClass);
                additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(behaviorClass));
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Failed to load pipeline behavior bean class", e);
            } catch (Exception ex) {
                LOGGER.errorf("failed to get requestClass for %s", behavior.name().toString());
            }
        }
        behaviorProducer.produce(new JmediatorPipelineBuildItem(behaviorClassNames));
    }

    private static String getRequestClass(ClassInfo handler) {
        for (Type interfaceType : handler.interfaceTypes()) {
            if (REQUEST_HANDLER_DOT_NAME.equals(interfaceType.name())) {
                return interfaceType.asParameterizedType().arguments().get(0).toString();
            }
        }
        return null;
    }

    @BuildStep
    @Record(RUNTIME_INIT)
    void configure(JmediatorRecorder recorder,
                   BeanContainerBuildItem beanContainer,
                   JmediatorHandlerBuildItem handlers,
                   JmediatorPipelineBuildItem behaviors) {
        recorder.initServiceFactory(beanContainer.getValue(), handlers.getHandlerClassNames(), behaviors.getBehaviorClassNames());
    }
}
