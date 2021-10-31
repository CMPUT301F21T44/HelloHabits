package com.github.cmput301f21t44.hellohabits.model;

import androidx.lifecycle.LiveData;

import java.time.Instant;
import java.util.List;

public interface HabitEventRepository {
    void insert(String habitId, String comment);

    void delete(HabitEvent habitEvent);

    LiveData<List<HabitEvent>> getEventsByHabitId(String habitId);

    HabitEvent update(String id, String habitId, Instant date, String comment, String photoPath, Location location);
}
