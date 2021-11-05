package com.github.cmput301f21t44.hellohabits.model;

import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.firebase.FirebaseTask;

import java.time.Instant;
import java.util.List;

public interface HabitEventRepository {
    /**
     * This function inserts a new habit event to the habit event list
     *
     * @param habitId a string of habit ID
     * @param comment a string of habit comment
     */
    void insert(String habitId, String comment, FirebaseTask.ThenFunction successCallback,
                FirebaseTask.CatchFunction failCallback);

    /**
     * This function deletes the given habit event
     *
     * @param habitEvent a HabitEvent to be deleted
     */
    void delete(HabitEvent habitEvent, FirebaseTask.CatchFunction failCallback);

    /**
     * this function returns a HabitEvent list by a habit ID
     *
     * @param habitId a habit with 0 or more habit events
     * @return the habit event list of the given habit
     */
    LiveData<List<HabitEvent>> getEventsByHabitId(String habitId);

    void update(String id, String habitId, Instant date, String comment,
                FirebaseTask.ResultFunction<HabitEvent> successCallback,
                FirebaseTask.CatchFunction failCallback);
}
