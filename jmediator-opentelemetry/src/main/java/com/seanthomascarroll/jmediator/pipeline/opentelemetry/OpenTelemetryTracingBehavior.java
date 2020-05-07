package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.opentelemetry.common.AttributeValue;
import io.opentelemetry.context.Scope;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Status;
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
        // TODO: handler name instead of request
        Span span = tracer.spanBuilder(request.getClass().getSimpleName()).startSpan();

        try (Scope scope = tracer.withSpan(span)) {
            return chain.doBehavior();
        } catch (Exception ex) {
            span.setStatus(Status.UNKNOWN.withDescription(ex.getLocalizedMessage()));
            span.addEvent("error", getExceptionDetails(ex));
            throw ex;
        } finally {
            span.end();
        }
    }

    private Map<String, AttributeValue> getExceptionDetails(Exception ex) {
        Map<String, AttributeValue> attributes = new HashMap<>(2);
        attributes.put("error.type", AttributeValue.stringAttributeValue(ex.getClass().getSimpleName()));
        attributes.put("error.message", AttributeValue.stringAttributeValue(ex.getMessage()));
        // TODO: add stacktrace as attribute
        // still waiting on PR for arrays
        // attributes.put("error.stack", ex.getStackTrace());
        return attributes;
    }
}
