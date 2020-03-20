package org.spoofer.cockpit.events;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidSensorFactory implements EventFactory {
    public static final int EVENT_TIME_POLL = 250 * 1000; // 250 m/sec
    private final SensorManager sensorManager;

    private final Map<String, SensorListener> connected = new HashMap<>();

    public AndroidSensorFactory(Context context) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void startListening(String name, EventListener listener) throws IllegalArgumentException {
        Sensor sensor = getSensor(name);
        if (sensor == null) {
            throw new IllegalArgumentException(String.format("%s is not a known name", name));
        }

        SensorListener listenerWrapper = connected.get(name);
        if (listenerWrapper == null) {
            listenerWrapper = new SensorListener(listener);
            connected.put(name, listenerWrapper);
        } else {
            listenerWrapper.listeners.add(listener);
        }

        if (!sensorManager.registerListener(listenerWrapper,
                sensor, EVENT_TIME_POLL, EVENT_TIME_POLL)) {
            throw new IllegalStateException(String.format("Failed to register listener on sensor %s", sensor.getName()));
        }
    }

    @Override
    public void stopListening(String name) {
        SensorListener listenerWrapper = connected.get(name);
        if (listenerWrapper == null)
            return;

        sensorManager.unregisterListener(listenerWrapper);
        connected.remove(name);
    }

    @Override
    public List<String> sourceNames() {
        List<String> names = new ArrayList<>();
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors) {
            names.add(sensor.getName());
        }
        return names;
    }

    private Sensor getSensor(String name) {
        Sensor sensor = null;
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : sensors) {
            if (s.getName().equals(name)) {
                sensor = s;
                break;
            }
        }
        return sensor;
    }


    class SensorListener implements SensorEventListener {

        private final List<EventListener> listeners = new ArrayList<>();


        public SensorListener(EventListener listener) {
            this.listeners.add(listener);
        }

        public List<EventListener> getListeners() {
            return listeners;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            Event ev = new Event(
                    event.sensor.getName(),
                    event.sensor.getStringType(),
                    event.values,
                    event.timestamp);

            for (EventListener listener : listeners)
                listener.update(ev);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }


}
