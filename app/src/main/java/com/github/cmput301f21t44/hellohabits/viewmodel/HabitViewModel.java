package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.firebase.FirebaseTask;
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.model.HabitRepository;

import java.time.Instant;
import java.util.List;

public class HabitViewModel extends ViewModel {
    private final HabitRepository mRepository;
    private final LiveData<List<Habit>> mAllHabits;
    private final MutableLiveData<Habit> mSelectedHabit = new MutableLiveData<>();

    public HabitViewModel(HabitRepository habitRepository) {
        mRepository = habitRepository;
        mAllHabits = mRepository.getAllHabits();
    }

    public void select(Habit habit) {
        mSelectedHabit.setValue(habit);
    }

    public LiveData<Habit> getSelectedHabit() {
        return mSelectedHabit;
    }

    public LiveData<List<Habit>> getAllHabits() {
        return mAllHabits;
    }

    public void insert(String name, String reason, Instant dateStarted, boolean[] daysOfWeek,
                       FirebaseTask.ThenFunction successCallback,
                       FirebaseTask.CatchFunction failCallback) {
        mRepository.insert(name, reason, dateStarted, daysOfWeek, successCallback, failCallback);
    }

    public void update(String id, String name, String reason, Instant dateStarted,
                       boolean[] daysOfWeek, FirebaseTask.ResultFunction<Habit> successCallback,
                       FirebaseTask.CatchFunction failCallback) {
        mRepository.update(id, name, reason, dateStarted, daysOfWeek, successCallback,
                failCallback);
    }

    public void delete(Habit habit, FirebaseTask.ThenFunction successCallback,
                       FirebaseTask.CatchFunction failCallback) {
        mRepository.delete(habit, successCallback, failCallback);
    }
}
