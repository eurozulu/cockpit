package org.spoofer.cockpit.dashboards;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.spoofer.cockpit.R;
import org.spoofer.cockpit.events.EventManager;
import org.spoofer.cockpit.sensorviews.SensorView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashFragment extends Fragment {

    public static final String ARG_LAYOUT = "arg_layout";
    @LayoutRes
    public static final int DEFAULT_DASH_LAYOUT = R.layout.fragment_dash;

    private EventManager eventManager;
    private List<SensorView> sensorViews;


    public DashFragment() {
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
        Bundle args = getArguments();
        if (args == null)
            args = new Bundle();

        @LayoutRes
        int layout = args.getInt(ARG_LAYOUT, DEFAULT_DASH_LAYOUT);
        // Inflate the layout for this fragment
        View v = inflater.inflate(layout, container, false);
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


    public static DashFragment NewDashFragment(@LayoutRes int layout) {
        Bundle b = new Bundle();
        b.putInt(ARG_LAYOUT, layout);
        DashFragment frag = new DashFragment();
        frag.setArguments(b);
        return frag;
    }
}
