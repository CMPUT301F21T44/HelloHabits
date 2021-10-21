package com.github.cmput301f21t44.hellohabits.model;

import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.db.HabitEntity;

import java.time.Instant;
import java.util.List;

public interface HabitRepository<T extends Habit> {
    LiveData<List<T>> getAllHabits();

    void insert(String title, String reason, Instant dateStarted);

    void delete(Habit habit);

}
