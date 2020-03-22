package org.spoofer.cockpit.events;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AndroidGPSFactory implements EventFactory {
    public static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 550;

    public static final String GPS_POSITION = "gps_position";
    public static final String GPS_ALTITUDE = "gps_altitude";
    public static final String GPS_BREARING = "gps_bearing";
    public static final String GPS_SPEED = "gps_speed";
    public static final String GPS_TIME = "gps_time";

    private static final String[] NAMES = new String[]{
            GPS_POSITION,
            GPS_ALTITUDE,
            GPS_BREARING,
            GPS_SPEED,
            GPS_TIME,
    };

    private static final long UPDATE_TIME = 250; // min milliseconds between updates
    private static final float UPDATE_DISTANCE = .5f; // min meters between updates

    private final Object lock = new Object();

    private final LocationManager locationManager;

    private final Map<String, GPSListener> gpsListeners = new HashMap<>();

    public AndroidGPSFactory(Context context) {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void startListening(String name, EventListener listener) throws IllegalArgumentException {
        GPSListener gpl = gpsListeners.get(name);
        if (gpl == null) {
            if (!sourceNames().contains(name))
                throw new IllegalArgumentException(String.format("%s is not a known gps name", name));
            LocationProvider provider = getProvider(name);
            if (provider == null) {
                return;
            }

            gpl = new GPSListener(name, provider);

            try {
                locationManager.requestLocationUpdates(gpl.locationProvider.getName(),
                        UPDATE_TIME,
                        UPDATE_DISTANCE,
                        gpl);
                gpsListeners.put(name, gpl);
            } catch (SecurityException e) {
                throw new IllegalStateException(e);
            }
        }
        gpl.eventListeners.add(listener);

    }

    @Override
    public void stopListening(String name) {
        GPSListener gpl = gpsListeners.get(name);
        if (gpl == null)
            return;
        gpsListeners.remove(name);
        locationManager.removeUpdates(gpl);
    }

    @Override
    public List<String> sourceNames() {
        return Arrays.asList(NAMES);
    }


    class GPSListener implements LocationListener {
        private final String name;
        private final LocationProvider locationProvider;
        private final List<EventListener> eventListeners = new ArrayList<>();

        public GPSListener(String name, LocationProvider locationProvider) {
            this.name = name;
            this.locationProvider = locationProvider;
        }

        @Override
        public void onLocationChanged(Location location) {
            Event ev = createEvent(location);
            for (EventListener listener : eventListeners)
                listener.update(ev);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        public void stop() {

        }

        private Event createEvent(Location location) {
            float[] values = getValues(name, location);
            Event ev = new Event(
                    name,
                    locationProvider.getName(),
                    values,
                    location.getTime());
            return ev;
        }
    }


    private float[] getValues(String name, Location location) {
        switch (name) {
            case GPS_ALTITUDE:
                return new float[]{(float) location.getAltitude(), location.getAccuracy()};
            case GPS_POSITION:
                return new float[]{(float) location.getLongitude(), (float) location.getLatitude(), location.getAccuracy()};
            case GPS_BREARING:
                return new float[]{location.getBearing(), location.getAccuracy()};
            case GPS_SPEED:
                return new float[]{location.getSpeed(), location.getAccuracy()};
            case GPS_TIME:
                long time = location.getTime();
                int days = (int) TimeUnit.MILLISECONDS.toDays(time);
                time -= (days * 24 * 60 * 60 * 1000);
                int hours = (int) TimeUnit.MILLISECONDS.toHours(time);
                time -= (hours * 60 * 60 * 1000);
                int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(time);
                time -= (minutes * 60 * 1000);
                int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(time);
                time -= (seconds * 1000);

                return new float[]{
                        hours, minutes, seconds, time, days
                };
            default:
                return null;
        }
    }

    private LocationProvider getProvider(String name) {
        Criteria criteria = new Criteria();
        String provName;

        switch (name) {
            case GPS_ALTITUDE:
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setAltitudeRequired(true);
                provName = locationManager.getBestProvider(criteria, false);
                return locationManager.getProvider(provName);
            case GPS_POSITION:
                criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_HIGH);
                provName = locationManager.getBestProvider(criteria, false);
                return locationManager.getProvider(provName);
            case GPS_BREARING:
                criteria.setBearingRequired(true);
                criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
                provName = locationManager.getBestProvider(criteria, false);
                return locationManager.getProvider(provName);
            case GPS_SPEED:
                criteria.setSpeedRequired(true);
                criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
                provName = locationManager.getBestProvider(criteria, false);
                return locationManager.getProvider(provName);
            case GPS_TIME:
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                provName = locationManager.getBestProvider(criteria, false);
                return locationManager.getProvider(provName);

            default:
                return null;
        }
    }


}
