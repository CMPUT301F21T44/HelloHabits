package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ResultFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;
import com.github.cmput301f21t44.hellohabits.model.habit.HabitRepository;
import com.github.cmput301f21t44.hellohabits.view.habit.HabitIndexChange;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ViewModel class for viewing all Habits
 * Also keeps track of the data on a selected Habit
 */
public class HabitViewModel extends ViewModel {
    /**
     * Repository class from which to fetch Habit data
     */
    private final HabitRepository mRepository;
    /**
     * LiveData list of a user's Habits
     */
    private final LiveData<List<Habit>> mAllHabits;
    /**
     * LiveData class for keeping track of a single HabitEvent's data
     */
    private final MutableLiveData<Habit> mSelectedHabit = new MutableLiveData<>();

    /**
     * Used to notify the Habit items in RecyclerView that the user is reordering habits
     */
    private final MutableLiveData<Boolean> mReordering = new MutableLiveData<>(false);

    /**
     * LiveData list of the user's habits today
     */
    private final MediatorLiveData<List<Habit>> mTodaysHabits = new MediatorLiveData<>();

    /**
     * Constructor for HabitViewModel
     * Accesses the data layer through dependency injection
     *
     * @param habitRepository HabitRepository from which to fetch data
     */
    public HabitViewModel(HabitRepository habitRepository) {
        mRepository = habitRepository;
        mAllHabits = mRepository.getAllHabits();
    }

    /**
     * Gets the currently selected habit
     *
     * @return LiveData of the selected Habit
     */
    public LiveData<Habit> getSelectedHabit() {
        return mSelectedHabit;
    }

    /**
     * Sets the currently selected habit
     *
     * @param habit Habit to keep track of
     */
    public void setSelectedHabit(Habit habit) {
        mSelectedHabit.setValue(habit);
    }

    /**
     * @return Reordering state LiveData
     */
    public LiveData<Boolean> getReordering() {
        return mReordering;
    }

    /**
     * Set the reordering state to the given value
     *
     * @param value true if reordering, false if not
     */
    public void setReordering(boolean value) {
        mReordering.setValue(value);
    }

    /**
     * @return LiveData List of the user's habits
     */
    public LiveData<List<Habit>> getAllHabits() {
        return mAllHabits;
    }

    /**
     * Retrieves Today's habits
     *
     * @param now current date
     * @return returns all habits for today's date
     */
    public LiveData<List<Habit>> getTodaysHabits(Instant now) {
        mTodaysHabits.removeSource(mAllHabits);
        mTodaysHabits.addSource(mAllHabits, habits -> {
            List<Habit> todaysHabits = new ArrayList<>();
            ZonedDateTime today = now.atZone(ZoneId.systemDefault());
            // traverse all h in habitList, and only masks in those who matches the checkBox
            // checkBox implementation can be seen in isInDay() from Habit.java
            for (Habit h : habits) {
                if (Habit.isInDay(today, h.getDaysOfWeek())) {
                    todaysHabits.add(h);
                }
            }
            Collections.sort(todaysHabits, Comparator.comparingInt(Habit::getIndex));
            mTodaysHabits.setValue(todaysHabits);
        });
        return mTodaysHabits;
    }

    /**
     * Get public habits of another user
     *
     * @param email Another user's email
     * @return LiveData of the list of public habits
     */
    public LiveData<List<Habit>> getUserPublicHabits(String email) {
        return mRepository.getUserPublicHabits(email);
    }

    /**
     * Inserts a habit in the repository
     *
     * @param title           title of the Habit
     * @param reason          Reason for the Habit
     * @param dateStarted     The starting date for the Habit
     * @param daysOfWeek      A boolean array of days of when the Habit is scheduled
     * @param isPrivate       Whether the habit is invisible to followers
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    public void insert(String title, String reason, Instant dateStarted, boolean[] daysOfWeek,
                       boolean isPrivate, ThenFunction successCallback,
                       CatchFunction failCallback) {
        mRepository.insert(title, reason, dateStarted, daysOfWeek, isPrivate, successCallback,
                failCallback);
    }

    /**
     * Updates a Habit in the repository
     *
     * @param id              UUID of the Habit
     * @param title           Title of the Habit
     * @param reason          Reason for the Habit
     * @param dateStarted     The starting date for the Habit
     * @param daysOfWeek      A boolean array of days of when the Habit is scheduled
     * @param isPrivate       Whether the habit is invisible to followers
     * @param index           The index of the habit in the user's list
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    public void update(String id, String title, String reason, Instant dateStarted,
                       boolean[] daysOfWeek, boolean isPrivate, int index,
                       ResultFunction<Habit> successCallback,
                       CatchFunction failCallback) {
        mRepository.update(id, title, reason, dateStarted, daysOfWeek, isPrivate, index,
                successCallback, failCallback);
    }

    /**
     * Delete a Habit from the repository
     *
     * @param habit           Habit to delete
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    public void delete(Habit habit, ThenFunction successCallback, CatchFunction failCallback) {
        mRepository.delete(habit, successCallback, failCallback);
    }

    /**
     * Updates the user's habit indices
     *
     * @param changeList   List of Habit index changes
     * @param failCallback Callback for when the operation fails
     */
    public void updateIndices(List<HabitIndexChange> changeList, CatchFunction failCallback) {
        mRepository.updateIndices(changeList, failCallback);
    }
}
