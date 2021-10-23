package com.github.cmput301f21t44.hellohabits.model;

import java.time.Instant;
import java.util.List;

public interface Habit {
    String getId();

    String getTitle();

    String getReason();

    Instant getDateStarted();

    List<HabitEvent> getEvents();
}
