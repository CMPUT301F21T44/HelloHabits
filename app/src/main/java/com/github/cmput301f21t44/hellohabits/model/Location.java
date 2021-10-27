package com.github.cmput301f21t44.hellohabits.model;

public interface Location {
    double getLongitude();

    double getLatitude();

    double getAccuracy();

    default boolean equals(Location other) {
        return this.getAccuracy() == other.getAccuracy()
                && this.getLatitude() == other.getLatitude()
                && this.getLongitude() == this.getLongitude();
    }
}
