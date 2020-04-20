package com.seanthomascarroll.jmediator.pipeline.prometheus;

import com.seanthomascarroll.jmediator.Request;
import com.seanthomascarroll.jmediator.pipeline.PipelineBehavior;
import com.seanthomascarroll.jmediator.pipeline.PipelineChain;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.SimpleTimer;
import io.prometheus.client.Summary;

public class PrometheusMetricsBehavior implements PipelineBehavior {

    private final Counter counter;
    private final Summary requestLatency;

    public PrometheusMetricsBehavior(CollectorRegistry registry) {
        this.counter = Counter.build()
            .name("app.request.count")
            .register(registry);

        this.requestLatency = Summary.build()
            // .quantile(0.5, 0.05)   // Add 50th percentile (= median) with 5% tolerated error
            // .quantile(0.9, 0.01)   // Add 90th percentile with 1% tolerated error
            .name("app_request_time")
            .help("Request latency in seconds")
            .register(registry);
    }


    @Override
    public <T extends Request> Object handle(T request, PipelineChain chain) {
        SimpleTimer requestTimer = new SimpleTimer();
        try {
            return chain.doBehavior();
        } finally {
            counter.inc();
            requestLatency.observe(requestTimer.elapsedSeconds());
        }
    }
}
