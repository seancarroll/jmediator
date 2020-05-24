package com.seanthomascarroll.jmediator.pipeline.honeycomb;

import io.honeycomb.libhoney.Event;
import io.honeycomb.libhoney.HoneyClient;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

// @RequestScope
// @Component

/**
 * TODO: details
 *
 * Commonly used as a request scoped bean / per request basis
 */
public class HoneycombContext {
//    @Autowired
//    private HoneyClient honeyClient;

    private HoneyClient honeyClient;
    private final Map<String, Object> contextualData;
    private final Event event;

    public HoneycombContext(HoneyClient honeyClient) {
        this.event = honeyClient.createEvent();
        this.contextualData = new HashMap<>();
    }

//    @PostConstruct
//    public void setup() {
//        this.event = honeyClient.createEvent();
//        this.contextualData = new HashMap<>();
//    }

    public Map<String, Object> getContextualData() {
        return contextualData;
    }

    public Event getEvent() {
        return event;
    }
}
