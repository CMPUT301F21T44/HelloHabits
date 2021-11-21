package com.github.cmput301f21t44.hellohabits.model.habit;

import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ResultFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;

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
     * Return a list of public habits of a user
     *
     * @param email The user's email
     * @return a list consisting of public habits
     */
    LiveData<List<Habit>> getUserPublicHabits(String email);

    /**
     * Creates a new Habit for the user
     *
     * @param title           Title of the Habit
     * @param reason          Reason for the Habit
     * @param dateStarted     The starting date for the Habit
     * @param daysOfWeek      A boolean array of days of when the Habit is scheduled
     * @param isPrivate       Whether the habit is invisible to followers
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void insert(String title, String reason, Instant dateStarted, boolean[] daysOfWeek,
                boolean isPrivate, ThenFunction successCallback,
                CatchFunction failCallback);

    /**
     * Delete the given Habit
     *
     * @param habit           Habit to delete
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void delete(Habit habit, ThenFunction successCallback,
                CatchFunction failCallback);

    /**
     * Updates a Habit with the given UUID
     *
     * @param id              UUID of the Habit
     * @param title           Title of the Habit
     * @param reason          Reason for the Habit
     * @param dateStarted     The starting date for the Habit
     * @param daysOfWeek      A boolean array of days of when the Habit is scheduled
     * @param isPrivate       Whether the habit is invisible to followers
     * @param index           Index of the Habit in the user's list
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void update(String id, String title, String reason, Instant dateStarted, boolean[] daysOfWeek,
                boolean isPrivate, int index, ResultFunction<Habit> successCallback,
                CatchFunction failCallback);
}
