package com.sobremesa.birdwatching.managers;

import android.location.Location;


import com.sobremesa.birdwatching.listeners.LocationListener;

import java.util.concurrent.CopyOnWriteArrayList;

public enum LocationManager {

    INSTANCE;

    private Location mLocation;

    private CopyOnWriteArrayList<LocationListener> mLocationListeners = new CopyOnWriteArrayList<LocationListener>();


    private LocationManager() {
    }


    public void setLocation(Location mLocation) {
        this.mLocation = mLocation;

        fireLocationEvent();
    }

    public Location getLocation() {
        return mLocation;
    }

    public void addLocationListener(LocationListener l) {
        mLocationListeners.add(l);
    }

    public void removeLocationListener(LocationListener l) {
        mLocationListeners.remove(l);
    }

    public void fireLocationEvent() {

        for (LocationListener l : mLocationListeners) {
            if (l != null)
                l.locationEventReceived();
        }
    }
}
