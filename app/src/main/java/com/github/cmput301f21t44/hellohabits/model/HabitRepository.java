package com.github.cmput301f21t44.hellohabits.model;

import androidx.lifecycle.LiveData;

import java.time.Instant;
import java.util.List;

public interface HabitRepository {
    /**
     * this function return a list of all habits
     *
     * @return a list consisting of all habits
     */
    LiveData<List<Habit>> getAllHabits();

    /**
     * This function insert a new habit into the list
     *
     * @param title       a string of habit title
     * @param reason      a string of habit reason
     * @param dateStarted an instant of start date
     * @param daysOfWeek  a boolean array of days
     */
    void insert(String title, String reason, Instant dateStarted, boolean[] daysOfWeek);

    /**
     * This function delete the given habit
     *
     * @param habit
     */
    void delete(Habit habit);

    /**
     * This function update the changes after changing an exiting habit
     *
     * @param id          the id of the given habit
     * @param title       a string of habit title
     * @param reason      a string of habit reason
     * @param dateStarted an instant of start date
     * @param daysOfWeek  a boolean array of days
     * @return the habit after editing
     */
    Habit update(String id, String title, String reason, Instant dateStarted, boolean[] daysOfWeek);
}
