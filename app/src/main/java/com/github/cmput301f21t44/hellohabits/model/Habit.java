package com.github.cmput301f21t44.hellohabits.model;

import java.time.Instant;
import java.util.List;

public interface Habit {
    static final int MONDAY = 0;
    static final int TUESDAY = 1;
    static final int WEDNESDAY = 2;
    static final int THURSDAY = 3;
    static final int FRIDAY = 4;
    static final int SATURDAY = 5;
    static final int SUNDAY = 6;

    String getId();

    String getTitle();

    String getReason();

    Instant getDateStarted();

    List<HabitEvent> getEvents();
    boolean[] getDaysOfWeek();
}
