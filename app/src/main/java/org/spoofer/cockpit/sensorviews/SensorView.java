package org.spoofer.cockpit.sensorviews;

import org.spoofer.cockpit.events.Event;
import org.spoofer.cockpit.events.EventListener;

public interface SensorView extends EventListener {

    // Gets the sensor name this view wishes to display.
    String getSensorName();

    @Override
    void update(Event event);

}
