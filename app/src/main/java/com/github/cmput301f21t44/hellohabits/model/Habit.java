package com.github.cmput301f21t44.hellohabits.model;

import java.time.Instant;
import java.util.List;

public interface Habit {
    static final int MONDAY = 1;
    static final int TUESDAY = 2;
    static final int WEDNESDAY = 3;
    static final int THURSDAY = 4;
    static final int FRIDAY = 5;
    static final int SATURDAY = 6;
    static final int SUNDAY = 0;

    String getId();

    String getTitle();

    String getReason();

    Instant getDateStarted();

    List<HabitEvent> getEvents();

    boolean[] getDaysOfWeek();

    static boolean isInDay(Instant today, boolean[] dayOfWeek) {
        // dayOfWeek goes from 1-7 (Monday-Sunday)
        int dayOfWeek = today.atZone(ZoneId.systemDefault()).getDayOfWeek().getValue();

        // daysOfWeek is an array that matches the checkBox for viewing.
        // in case today is Monday:
        // [true, true ,true, true, true, true, true] = every day
        // [true, false , false, false, false, false, false] = only on monday
        // [true, false , false, false, false, false, true] = for monday and sunday 
        return daysOfWeek[dayOfWeek-1]
    }


}
