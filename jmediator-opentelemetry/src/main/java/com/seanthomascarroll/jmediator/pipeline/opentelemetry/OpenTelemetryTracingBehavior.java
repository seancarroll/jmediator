package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

public class OpenTelemetryTracingBehavior implements PipelineBehavior {

    private final Tracer tracer;

    public OpenTelemetryTracingBehavior(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Object handle(Request request, PipelineChain chain) {
        Span span = tracer.spanBuilder(request.getClass().getSimpleName()).startSpan();
        try (Scope scope = span.makeCurrent()) {
            return chain.doBehavior(request);
        } catch (Exception ex) {
            span.recordException(ex, getExceptionDetails(ex));
            span.setStatus(StatusCode.ERROR, ex.getLocalizedMessage());
            throw ex;
        } finally {
            span.end();
        }
    }

    private Attributes getExceptionDetails(Exception ex) {
        return Attributes.builder()
            .put("error.type", ex.getClass().getSimpleName())
            .put("error.message", ex.getMessage())
            .build();
    }
}
