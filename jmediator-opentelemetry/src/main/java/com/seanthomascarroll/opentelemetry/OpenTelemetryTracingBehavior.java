package com.seanthomascarroll.opentelemetry;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.opentelemetry.context.Scope;
import io.opentelemetry.trace.AttributeValue;
import io.opentelemetry.trace.Event;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Tracer;

import java.util.HashMap;
import java.util.Map;

public class OpenTelemetryTracingBehavior implements PipelineBehavior {

    private final Tracer tracer;

    public OpenTelemetryTracingBehavior(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public <T extends Request> Object handle(T request, PipelineChain chain) {
        Span span = tracer.spanBuilder(request.getClass().getSimpleName()).startSpan();

        // TODO: what tags / logs / baggage should we add?
        // Tags are key:value pairs that enable user-defined annotation of spans in order to query, filter, and comprehend trace data.
        // Span tags should apply to the whole span.
        // Logs are key:value pairs that are useful for capturing timed log messages and other debugging or informational output from the application itself. Logs may be useful for documenting a specific moment or event within the span (in contrast to tags which should apply to the span regardless of time).
        try (Scope scope = tracer.withSpan(span)) {
            // Tags.COMPONENT.set(span, request.getClass().getSimpleName());
            // TODO: Do we want to log anything after?
            return chain.doBehavior();
        } catch (Exception ex) {
            // Tags.ERROR.set(span, true);
            // span.log(getExceptionDetails(ex));
            throw ex;
        } finally {
            span.end();
        }
    }

    private Map<String, Object> getExceptionDetails(Exception ex) {
        Map<String, AttributeValue> map = new HashMap<>(4);

        Map<String, Object> m = new HashMap<>(4);
//        m.put(Fields.EVENT, "error");
//        m.put(Fields.ERROR_OBJECT, ex);
//        m.put(Fields.MESSAGE, ex.getMessage());
//        m.put(Fields.STACK, ex.getStackTrace());
        return m;
    }
}
