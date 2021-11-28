package com.github.cmput301f21t44.hellohabits.model.habitevent;

import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ResultFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;

import java.time.Instant;
import java.util.List;

/**
 * Interface to implement habit event repository functions
 */
public interface HabitEventRepository {
    /**
     * Inserts a new habit event to the HabitEvent list of a Habit with the given UUID
     *
     * @param habitId         UUID of the Habit parent
     * @param comment         Optional comment
     * @param photoPath       Optional photo path
     * @param location        Optional location
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void insert(String habitId, String comment, String photoPath, Location location,
                ThenFunction successCallback, CatchFunction failCallback);

    /**
     * Deletes a given HabitEvent
     *
     * @param habitEvent   HabitEvent to delete
     * @param failCallback Callback for when the operation fails
     */
    void delete(HabitEvent habitEvent, CatchFunction failCallback);

    /**
     * Returns a HabitEvent list owned by a Habit with a given ID
     *
     * @param habitId UUID of the Habit
     * @return The HabitEvent list of the given habit
     */
    LiveData<List<HabitEvent>> getEventsByHabitId(String habitId);

    /**
     * Updates a HabitEvent with a given UUID
     *
     * @param id              UUID of the HabitEvent
     * @param habitId         UUID of the Habit parent
     * @param date            Instant of when the HabitEvent was denoted
     * @param comment         Optional comment
     * @param photoPath       Optional photo path
     * @param location        Optional location
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void update(String id, String habitId, Instant date, String comment,
                String photoPath, Location location, ResultFunction<HabitEvent> successCallback,
                CatchFunction failCallback);
}
