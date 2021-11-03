package com.github.cmput301f21t44.hellohabits.db.habitevent;

import androidx.room.ColumnInfo;

import com.github.cmput301f21t44.hellohabits.model.Location;

/**
 * Location class embedded into HabitEventEntity
 */
public class DBLocation implements Location {
    @ColumnInfo(name = "longitude")
    public double mLongitude;
    @ColumnInfo(name = "latitude")
    public double mLatitude;
    @ColumnInfo(name = "accuracy")
    public double mAccuracy;

    public DBLocation(double longitude, double latitude, double accuracy) {
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mAccuracy = accuracy;
    }


    public double getLongitude() {
        return mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getAccuracy() {
        return mAccuracy;
    }

    public static DBLocation from(Location l) {
        if (l == null) return null;
        return new DBLocation(l.getLongitude(), l.getLatitude(), l.getAccuracy());
    }
}
