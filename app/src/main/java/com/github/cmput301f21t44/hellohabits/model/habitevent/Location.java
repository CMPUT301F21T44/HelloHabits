package com.github.cmput301f21t44.hellohabits.model.habitevent;

/**
 * Interface to implement a HabitEvent Location
 */
public interface Location {
    double getLongitude();

    double getLatitude();

    double getAccuracy();

    /**
     * This function tells if a location is equal to the other location
     *
     * @param other the location to be compared to
     * @return a boolean value indicating if two locations are equal to each other
     */
    default boolean equals(Location other) {
        return this.getAccuracy() == other.getAccuracy()
                && this.getLatitude() == other.getLatitude()
                && this.getLongitude() == this.getLongitude();
    }
}
