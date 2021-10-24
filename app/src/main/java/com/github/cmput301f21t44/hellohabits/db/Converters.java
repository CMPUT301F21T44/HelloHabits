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

    @TypeConverter
    public static boolean[] toBoolArray(byte value) {
        boolean[] array = new boolean[7];
        for (int i = 0; i < 7; ++i) {
            array[i] = ((value) & (1 << i)) != 0;
        }
        return array;
    }

    @TypeConverter
    public static byte fromBoolArray(boolean[] array) {
        byte value = 0;
        for (int i = 0; i < 7; ++i) {
            value |= (byte) ((array[i] ? 1 : 0) << i);
        }
        return value;
    }
}
