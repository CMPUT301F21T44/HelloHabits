package com.github.cmput301f21t44.hellohabits.firebase;

import com.github.cmput301f21t44.hellohabits.model.habitevent.Location;

import java.io.Serializable;

/**
 * Firestore implementation of Location
 */
class FSLocation implements Location, Serializable {
    private double mLongitude;
    private double mLatitude;
    private double mAccuracy;

    public FSLocation() {
        // Needed for Firestore conversions
    }

    /**
     * Create a FSLocation
     *
     * @param longitude double value of location longitude
     * @param latitude  double value of location latitude
     * @param accuracy  double value of location accuracy
     */
    public FSLocation(double longitude, double latitude, double accuracy) {
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mAccuracy = accuracy;
    }

    @Override
    public double getLongitude() {
        return mLongitude;
    }

    @Override
    public double getLatitude() {
        return mLatitude;
    }

    @Override
    public double getAccuracy() {
        return mAccuracy;
    }
}
