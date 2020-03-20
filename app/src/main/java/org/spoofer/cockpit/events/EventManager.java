package org.spoofer.cockpit.events;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

    private final EventFactory[] factories;

    private final Map<String, List<EventListener>> listeners = new HashMap<>();

    private final List<String> connected = new ArrayList<>();


    public EventManager(Context context) {
        super();
        factories = new EventFactory[]{
                new AndroidSensorFactory(context)
        };
    }

    public void startListeners() {
        for (String key : listeners.keySet()) {
            startListener(key);
        }
    }

    public void startListener(String name) {
        if (connected.contains(name)) {
            return;
        }
        List<EventListener> lsnrs = listeners.get(name);
        if (lsnrs.isEmpty())
            return;

        EventFactory factory = getFactoryForName(name);
        if (null == factory) {
            throw new IllegalArgumentException(String.format("%s is not a known name", name));
        }

        for (EventListener l : lsnrs) {
            factory.startListening(name, l);
        }
        connected.add(name);
    }

    public void stopListeners() {
        for (String key : connected) {
            stopListener(key);
        }
    }

    public void stopListener(String name) {
        EventFactory factory = getFactoryForName(name);
        if (null == factory) {
            throw new IllegalArgumentException(String.format("%s is not a known name", name));
        }
        factory.stopListening(name);
        connected.remove(name);

    }

    public void addListener(String name, EventListener listener) throws IllegalArgumentException {
        List<String> knownNames = getNames();
        if (!knownNames.contains(name)) {
            throw new IllegalArgumentException(String.format("%s is not a known sensor name"));
        }

        List<EventListener> val = listeners.get(name);
        if (null == val) {
            val = new ArrayList<>();
            listeners.put(name, val);
        }

        if (!val.contains(listener))
            val.add(listener);

    }

    public void removeListener(EventListener listener) {
        for (Map.Entry<String, List<EventListener>> entry : listeners.entrySet()) {
            if (!entry.getValue().contains(listener))
                continue;

            if (connected.contains(entry.getKey())) {
                getFactoryForName(entry.getKey()).stopListening(entry.getKey());
            }

            entry.getValue().remove(listener);
            if (entry.getValue().isEmpty()) {
                listeners.remove(entry.getKey());
            }
        }
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (EventFactory factory : factories) {
            names.addAll(factory.sourceNames());
        }
        return names;
    }

    private EventFactory getFactoryForName(String name) {
        EventFactory factory = null;
        for (EventFactory facty : factories) {
            if (facty.sourceNames().contains(name)) {
                factory = facty;
                break;
            }
        }
        return factory;
    }

}


