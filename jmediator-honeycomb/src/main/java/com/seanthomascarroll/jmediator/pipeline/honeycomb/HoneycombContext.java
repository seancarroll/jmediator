package com.seanthomascarroll.jmediator.pipeline.honeycomb;

import io.honeycomb.libhoney.Event;

import java.util.HashMap;
import java.util.Map;

// @RequestScope
// @Component
public class HoneycombContext {
//    @Autowired
//    private HoneyClient honeyClient;

    private Map<String, Object> contextualData;
    private Event event;

    // @PostConstruct
    public void setup() {
        // this.event = honeyClient.createEvent();
        this.contextualData = new HashMap<>();
    }

    public Map<String, Object> getContextualData() {
        return contextualData;
    }

    public Event getEvent() {
        return event;
    }
}
