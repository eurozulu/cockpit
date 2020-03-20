package org.spoofer.cockpit.events;

import java.util.List;
// EventFactory generates events from an underlying provider
public interface EventFactory {
    void startListening(String name, EventListener listener) throws IllegalArgumentException;
    void stopListening(String name);

    List<String> sourceNames();
}
