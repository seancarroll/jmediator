package com.seanthomascarroll.jmediator.pipeline.honeycomb;

import io.honeycomb.libhoney.Event;
import io.honeycomb.libhoney.HoneyClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds a Honeycomb {@link Event} and map of contextual data which can be passed around
 * to components so that they can add to the end which will ultimately be sent to Honeycomb.
 *
 * This class should be a tied to a single HTTP request or a request scoped bean
 *
 */
public class HoneycombContext {
    private final Map<String, Object> contextualData;
    private final Event event;

    public HoneycombContext(HoneyClient honeyClient) {
        this.event = honeyClient.createEvent();
        this.contextualData = new HashMap<>();
    }

    public Map<String, Object> getContextualData() {
        return contextualData;
    }

    public Event getEvent() {
        return event;
    }
}
