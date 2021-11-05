package com.github.cmput301f21t44.hellohabits.model;

import androidx.annotation.NonNull;

/**
 * Class to store the days of week for habits
 */
public final class DaysOfWeek {
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

}
