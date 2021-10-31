package com.github.cmput301f21t44.hellohabits.model;

import androidx.lifecycle.LiveData;

import java.time.Instant;
import java.util.List;

public interface HabitRepository {
    LiveData<List<Habit>> getAllHabits();

    void insert(String title, String reason, Instant dateStarted, boolean[] daysOfWeek);

    void delete(Habit habit);

    Habit update(String id, String title, String reason, Instant dateStarted, boolean[] daysOfWeek);
}
