package org.spoofer.cockpit.events;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private final String name;
    private final String type;
    private final float[] values;

    private final long timestamp;

    private final Map<String, String> metadata = new HashMap<>();

    public Event(String name, String type, float[] values, long timestamp) {
        this.name = name;
        this.type = type;
        this.values = values;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float[] getValues() {
        return values;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}

