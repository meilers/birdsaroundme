package com.sobremesa.birdwatching.managers;

import android.location.Location;


import java.util.concurrent.CopyOnWriteArrayList;

public enum LocationManager {

    INSTANCE;

    private Location mLocation;


    private LocationManager() {
    }


    public void setLocation(Location mLocation) {
        this.mLocation = mLocation;
    }

    public Location getLocation() {
        return mLocation;
    }
}
