package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ResultFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;
import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEventRepository;
import com.github.cmput301f21t44.hellohabits.model.habitevent.Location;

import java.time.Instant;
import java.util.List;

/**
 * ViewModel class for viewing a Habit's events
 * Also keeps track of the data on a selected HabitEvent
 */
public class HabitEventViewModel extends ViewModel {
    /**
     * Repository class from which to fetch HabitEvent data
     */
    private final HabitEventRepository mRepository;
    /**
     * LiveData class for keeping track of a single HabitEvent's data
     */
    private final MutableLiveData<HabitEvent> mSelectedEvent = new MutableLiveData<>();

    /**
     * Constructor for HabitEventViewModel
     * Accesses the data layer through dependency injection
     *
     * @param habitEventRepository HabitEventRepository from which to fetch data
     */
    public HabitEventViewModel(HabitEventRepository habitEventRepository) {
        mRepository = habitEventRepository;
    }

    /**
     * Gets a HabitEvent list owned by a Habit with a given ID from the repository
     *
     * @param habitId UUID of the Habit
     * @return The HabitEvent list of the given habit
     */
    public LiveData<List<HabitEvent>> getHabitEventsById(String habitId) {
        return mRepository.getEventsByHabitId(habitId);
    }

    /**
     * Inserts a habit event in the repository
     *
     * @param habitId         UUID of the Habit parent
     * @param comment         Optional comment
     * @param photoPath       Optional photo path
     * @param location        Optional location
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    public void insert(String habitId, String comment, String photoPath, Location location,
                       ThenFunction successCallback, CatchFunction failCallback) {
        mRepository.insert(habitId, comment, photoPath, location, successCallback, failCallback);
    }

    /**
     * Updates a habit event in the repository
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
    public void update(String id, String habitId, Instant date, String comment,
                       String photoPath, Location location,
                       ResultFunction<HabitEvent> successCallback,
                       CatchFunction failCallback) {
        mRepository.update(id, habitId, date, comment, photoPath, location, successCallback,
                failCallback);
    }

    /**
     * Deletes a habit event from the repository
     *
     * @param habitEvent   HabitEvent to delete
     * @param failCallback Callback for when the operation fails
     */
    public void delete(HabitEvent habitEvent, CatchFunction failCallback) {
        mRepository.delete(habitEvent, failCallback);
    }

    /**
     * Gets the currently selected event
     *
     * @return LiveData of the selected HabitEvent
     */
    public LiveData<HabitEvent> getSelectedEvent() {
        return mSelectedEvent;
    }

    /**
     * Sets the currently selected event
     *
     * @param habitEvent HabitEvent to keep track of
     */
    public void setSelectedEvent(HabitEvent habitEvent) {
        mSelectedEvent.setValue(habitEvent);
    }
}
