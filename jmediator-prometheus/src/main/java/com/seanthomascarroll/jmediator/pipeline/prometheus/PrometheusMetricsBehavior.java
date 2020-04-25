package com.seanthomascarroll.jmediator.pipeline.prometheus;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Summary;

public class PrometheusMetricsBehavior implements PipelineBehavior {

    private final Summary requestSummary;

    public PrometheusMetricsBehavior(CollectorRegistry registry) {
        this.requestSummary = Summary.build()
            .quantile(0.5, 0.05)   // Add 50th percentile (= median) with 5% tolerated error
            .quantile(0.9, 0.01)   // Add 90th percentile with 1% tolerated error
            .name("request_time")
            .labelNames("request_name")
            .help("Request latency in seconds")
            .register(registry);
    }


    @Override
    public <T extends Request> Object handle(T request, PipelineChain chain) {
        Summary.Timer timer = requestSummary.labels(request.getClass().getName()).startTimer();
        try {
            return chain.doBehavior();
        } finally {
            timer.observeDuration();
        }
    }
}
