package org.spoofer.cockpit.views;

import org.spoofer.cockpit.events.EventListener;

import java.util.List;

public interface SensorView extends EventListener {
    // Gets a list of the sensor names this view wishes to be updated on.
    List<String> getSensorNames();
}
