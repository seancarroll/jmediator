package com.seanthomascarroll.jmediator.pipeline.opentelemetry;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.opentelemetry.common.AttributeValue;
import io.opentelemetry.common.Attributes;
import io.opentelemetry.context.Scope;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Status;
import io.opentelemetry.trace.Tracer;

public class OpenTelemetryTracingBehavior implements PipelineBehavior {

    private final Tracer tracer;

    public OpenTelemetryTracingBehavior(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Object handle(Request request, PipelineChain chain) {
        Span span = tracer.spanBuilder(request.getClass().getSimpleName()).startSpan();

        try (Scope scope = tracer.withSpan(span)) {
            return chain.doBehavior(request);
        } catch (Exception ex) {
            span.setStatus(Status.UNKNOWN.withDescription(ex.getLocalizedMessage()));
            span.addEvent("error", getExceptionDetails(ex));
            throw ex;
        } finally {
            span.end();
        }
    }

    private Attributes getExceptionDetails(Exception ex) {
        // TODO: add stacktrace as attribute
        // currently not supported by opentelemetry. see https://github.com/open-telemetry/opentelemetry-java/issues/243
        // .setAttribute("error.stack", ex.getStackTrace());
        return Attributes.newBuilder()
            .setAttribute("error.type", AttributeValue.stringAttributeValue(ex.getClass().getSimpleName()))
            .setAttribute("error.message", AttributeValue.stringAttributeValue(ex.getMessage()))
            .build();
    }
}
