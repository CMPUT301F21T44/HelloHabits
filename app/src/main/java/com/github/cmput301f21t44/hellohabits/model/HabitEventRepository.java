package com.github.cmput301f21t44.hellohabits.model;

import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.firebase.FirebaseTask;

import java.time.Instant;
import java.util.List;

public interface HabitEventRepository {
    void insert(String habitId, String comment, FirebaseTask.ThenFunction successCallback,
                FirebaseTask.CatchFunction failCallback);

    void delete(HabitEvent habitEvent, FirebaseTask.CatchFunction failCallback);

    LiveData<List<HabitEvent>> getEventsByHabitId(String habitId);

    void update(String id, String habitId, Instant date, String comment,
                FirebaseTask.ResultFunction<HabitEvent> successCallback,
                FirebaseTask.CatchFunction failCallback);
}
