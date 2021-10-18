package com.github.cmput301f21t44.hellohabits.db;

import androidx.room.TypeConverter;

import java.time.Instant;

// https://developer.android.com/training/data-storage/room/referencing-data

public class Converters {
    @TypeConverter
    public static Instant toInstant(String value) {
        return value == null ? null : Instant.parse(value);
    }

    @TypeConverter
    public static String fromInstant(Instant instant) {
        return instant == null ? null : instant.toString();
    }
}
