package com.github.cmput301f21t44.hellohabits.model;

import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.firebase.FirebaseTask;

import java.time.Instant;
import java.util.List;

public interface HabitRepository {
    LiveData<List<Habit>> getAllHabits();

    void insert(String title, String reason, Instant dateStarted, boolean[] daysOfWeek,
                FirebaseTask.ThenFunction successCallback, FirebaseTask.CatchFunction failCallback);

    void delete(Habit habit, FirebaseTask.ThenFunction successCallback,
                FirebaseTask.CatchFunction failCallback);

    void update(String id, String title, String reason, Instant dateStarted, boolean[] daysOfWeek,
                FirebaseTask.ResultFunction<Habit> successCallback,
                FirebaseTask.CatchFunction failCallback);
}
