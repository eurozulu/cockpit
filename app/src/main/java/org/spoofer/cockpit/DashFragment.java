package org.spoofer.cockpit;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import org.spoofer.cockpit.events.EventManager;
import org.spoofer.cockpit.views.SensorView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashFragment extends Fragment {
    public static final String ARG_LAYOUT = "arg_layout";

    private EventManager eventManager;
    private List<SensorView> sensorViews;

    private ArrayAdapter<String> sensorSelection;

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

        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if (args == null)
            args = new Bundle();

        @LayoutRes
        int layout = args.getInt(ARG_LAYOUT, R.layout.fragment1_dash);

        // Inflate the layout for this fragment
        View v = inflater.inflate(layout, container, false);
        if (!(v instanceof ViewGroup))
            throw new IllegalStateException("Expected dash parent view to be a ViewGroup");
        ViewGroup parent = (ViewGroup) v;

        sensorViews = findSensorViews(parent);
        registerViews(sensorViews);

        List<String> names = eventManager.getNames();
        String[] sa = new String[names.size()];
        names.toArray(sa);

        sensorSelection = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line,
                sa);
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


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_layout, menu);
        MenuItem item = menu.findItem(R.id.spinner_sensor);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        spinner.setAdapter(sensorSelection); // set the adapter to provide layout of rows and content
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), sensorSelection.getItem(position), Toast.LENGTH_LONG).show();
                setSensorName(sensorSelection.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    private void setSensorName(String name) {
        eventManager.stopListeners();
        deregisterViews(sensorViews);
        for (SensorView sv : sensorViews) {
            sv.setSensorName(name);
        }
        registerViews(sensorViews);
        eventManager.startListeners();
    }

    public static DashFragment NewDashFragment(@LayoutRes int layout) {
        Bundle b = new Bundle();
        b.putInt(ARG_LAYOUT, layout);
        DashFragment frag = new DashFragment();
        frag.setArguments(b);
        return frag;
    }
}
