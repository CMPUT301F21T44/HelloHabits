package com.github.cmput301f21t44.hellohabits.model.habit;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store the days of week for habits
 */
public final class DaysOfWeek {
    /**
     * Array of String literals for representing days as text
     */
    public static final String[] shorthandDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    /**
     * Returns an empty boolean array
     *
     * @return a empty boolean array
     */
    public static boolean[] emptyArray() {
        return new boolean[]{false, false, false, false, false, false, false};
    }

    /**
     * Converts a bool array of days into a space-separated string with shorthand representation
     *
     * @param daysOfWeek Boolean array of days
     * @return A string containing the days separated by spaces
     */
    @NonNull
    public static String toString(boolean[] daysOfWeek) {
        assert (daysOfWeek.length == shorthandDays.length);
        StringBuilder onDays = new StringBuilder();
        for (int i = 0; i < 7; ++i) {
            if (daysOfWeek[i]) {
                if (onDays.length() != 0) {
                    onDays.append(" ");
                }
                onDays.append(shorthandDays[i]);
            }
        }
        return onDays.toString();
    }

    /**
     * Convert a Boolean List to a boolean array
     *
     * @param dayList List of Booleans
     * @return Array of booleans
     */
    public static boolean[] fromList(List<Boolean> dayList) {
        boolean[] daysOfWeek = new boolean[7];
        assert dayList != null;
        for (int i = 0; i < 7; ++i) {
            daysOfWeek[i] = dayList.get(i);
        }
        return daysOfWeek;
    }

    /**
     * Convert a boolean array to a Boolean List
     *
     * @param daysOfWeek boolean array
     * @return List of Booleans
     */
    public static List<Boolean> toList(boolean[] daysOfWeek) {
        List<Boolean> days = new ArrayList<>();
        for (boolean b : daysOfWeek) {
            days.add(b);
        }
        return days;
    }
}
