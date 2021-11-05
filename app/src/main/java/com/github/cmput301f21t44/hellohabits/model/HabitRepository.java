package com.github.cmput301f21t44.hellohabits.model;

import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.firebase.FirebaseTask;

import java.time.Instant;
import java.util.List;

/**
 * Interface to implement habit repository methods
 */
public interface HabitRepository {
    /**
     * Return a list of all habits of the current user
     *
     * @return a list consisting of all habits
     */
    LiveData<List<Habit>> getAllHabits();

    /**
     * Creates a new Habit for the user
     *
     * @param title           Title of the Habit
     * @param reason          Reason for the Habit
     * @param dateStarted     The starting date for the Habit
     * @param daysOfWeek      A boolean array of days of when the Habit is scheduled
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void insert(String title, String reason, Instant dateStarted, boolean[] daysOfWeek,
                FirebaseTask.ThenFunction successCallback, FirebaseTask.CatchFunction failCallback);

    /**
     * Delete the given Habit
     *
     * @param habit           Habit to delete
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void delete(Habit habit, FirebaseTask.ThenFunction successCallback,
                FirebaseTask.CatchFunction failCallback);

    /**
     * Updates a Habit with the given UUID
     *
     * @param id              UUID of the Habit
     * @param title           Title of the Habit
     * @param reason          Reason for the Habit
     * @param dateStarted     The starting date for the Habit
     * @param daysOfWeek      A boolean array of days of when the Habit is scheduled
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void update(String id, String title, String reason, Instant dateStarted, boolean[] daysOfWeek,
                FirebaseTask.ResultFunction<Habit> successCallback,
                FirebaseTask.CatchFunction failCallback);
}
