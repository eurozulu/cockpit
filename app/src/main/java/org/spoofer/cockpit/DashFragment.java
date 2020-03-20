package org.spoofer.cockpit;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.spoofer.cockpit.events.EventManager;
import org.spoofer.cockpit.views.SensorView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashFragment extends Fragment {

    private EventManager eventManager;
    private List<SensorView> sensorViews;

    public DashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        eventManager = new EventManager(context);
    }

    @Override
    public void onDestroy() {
        eventManager.stopListeners();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dash, container, false);
        if (!(v instanceof ViewGroup))
            throw new IllegalStateException("Expected dash parent view to be a ViewGroup");
        ViewGroup parent = (ViewGroup) v;

        sensorViews = findSensorViews(parent);
        registerViews(sensorViews);
        return parent;
    }

    @Override
    public void onDestroyView() {
        deregisterViews(sensorViews);
        sensorViews.clear();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        eventManager.startListeners();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        eventManager.stopListeners();
    }

    private List<SensorView> findSensorViews(ViewGroup parent) {
        List<SensorView> found = new ArrayList<>();
        for (int index = 0; index < parent.getChildCount(); index++) {
            View v = parent.getChildAt(index);
            if (v instanceof SensorView) {
                found.add((SensorView) v);
                continue;
            }

            if (v instanceof ViewGroup) {
                found.addAll(findSensorViews((ViewGroup) v));
                continue;
            }
            // Not a sensor view, ignore it
        }
        return found;
    }

    private void registerViews(List<SensorView> sensorViews) {
        for (SensorView v : sensorViews) {
            eventManager.addListener(v.getSensorName(), v);
        }
    }
    private void deregisterViews(List<SensorView> sensorViews) {
        for (SensorView v : sensorViews) {
            eventManager.removeListener(v);
        }
    }
}
