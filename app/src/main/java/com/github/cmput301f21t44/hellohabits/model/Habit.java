package com.github.cmput301f21t44.hellohabits.model;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

public interface Habit {


    String getId();

    String getTitle();

    String getReason();

    Instant getDateStarted();

    List<HabitEvent> getEvents();

    boolean[] getDaysOfWeek();

    static boolean isInDay(ZonedDateTime today, boolean[] daysOfWeek) {
        // dayOfWeek goes from 1-7 (Monday-Sunday)
        int dayOfWeek = today.getDayOfWeek().getValue();

        // daysOfWeek is an array that matches the checkBox for viewing.
        // in case today is Monday:
        // [true, true ,true, true, true, true, true] = every day
        // [true, false , false, false, false, false, false] = only on monday
        // [true, false , false, false, false, false, true] = for monday and sunday 
        return daysOfWeek[dayOfWeek - 1];
    }


}
