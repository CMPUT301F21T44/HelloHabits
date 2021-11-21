package com.github.cmput301f21t44.hellohabits.firebase;

import com.github.cmput301f21t44.hellohabits.model.habitevent.Location;

import java.io.Serializable;

class FSLocation implements Location, Serializable {
    private final double longitude;
    private final double latitude;
    private final double accuracy;

    public FSLocation(double longitude, double latitude, double accuracy) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.accuracy = accuracy;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getAccuracy() {
        return accuracy;
    }
}
