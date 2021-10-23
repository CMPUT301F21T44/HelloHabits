package com.github.cmput301f21t44.hellohabits.model;

import java.time.Instant;

public interface HabitEvent {
    String getId();

    String getHabitId();

    Instant getDate();

    String getComment();

    String getPhotoPath();

    Location getLocation();
}
