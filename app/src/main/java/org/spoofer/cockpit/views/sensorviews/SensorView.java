package org.spoofer.cockpit.views.sensorviews;

import org.spoofer.cockpit.events.EventListener;

public interface SensorView extends EventListener {
    // Gets the sensor name this view wishes to be updated on.
    String getSensorName();

    void setSensorName(String sensorName);
}
