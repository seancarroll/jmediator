package jmediator.opentracing.pipeline;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import jmediator.Request;
import jmediator.pipeline.PipelineBehavior;
import jmediator.pipeline.PipelineChain;

import java.util.HashMap;
import java.util.Map;

// https://github.com/opentracing/opentracing-java
// https://opentracing.io/guides/java/
// https://github.com/opentracing-contrib/java-opentracing-walkthrough
// https://github.com/yurishkuro/opentracing-tutorial/tree/master/java

/**
 *
 */
public class OpenTracingBehavior implements PipelineBehavior {

    private final Tracer tracer;

    public OpenTracingBehavior(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public <T extends Request> Object handle(T request, PipelineChain chain) {
        Span span = tracer.buildSpan(request.getClass().getSimpleName()).start();

        // TODO: what tags / logs / baggage should we add?
        // Tags are key:value pairs that enable user-defined annotation of spans in order to query, filter, and comprehend trace data.
        // Span tags should apply to the whole span.
        // Logs are key:value pairs that are useful for capturing timed log messages and other debugging or informational output from the application itself. Logs may be useful for documenting a specific moment or event within the span (in contrast to tags which should apply to the span regardless of time).
        try (Scope scope = tracer.scopeManager().activate(span)) {
            // Tags.COMPONENT.set(span, request.getClass().getSimpleName());
            // TODO: Do we want to log anything after?
            return chain.doBehavior();
        } catch(Exception ex) {
            Tags.ERROR.set(span, true);
            span.log(getExceptionDetails(ex));
            throw ex;
        } finally {
            span.finish();
        }
    }

    private Map<String, Object> getExceptionDetails(Exception ex) {
        Map<String, Object> m = new HashMap<>(4);
        m.put(Fields.EVENT, "error");
        m.put(Fields.ERROR_OBJECT, ex);
        m.put(Fields.MESSAGE, ex.getMessage());
        m.put(Fields.STACK, ex.getStackTrace());
        return m;
    }
}
