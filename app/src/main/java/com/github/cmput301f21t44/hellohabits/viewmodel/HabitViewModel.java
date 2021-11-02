package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.model.HabitRepository;

import java.time.Instant;
import java.util.List;

public class HabitViewModel extends ViewModel {
    private final HabitRepository mRepository;
    private final LiveData<List<Habit>> mAllHabits;

    private final MutableLiveData<Habit> selected = new MutableLiveData<>();

    public HabitViewModel(HabitRepository habitRepository) {
        mRepository = habitRepository;
        mAllHabits = mRepository.getAllHabits();
    }

    public void select(Habit habit) {
        selected.setValue(habit);
    }

    public LiveData<Habit> getSelected() {
        return selected;
    }

    public LiveData<List<Habit>> getAllHabits() {
        return mAllHabits;
    }

    public void insert(String name, String reason, Instant dateStarted, boolean[] daysOfWeek) {
        mRepository.insert(name, reason, dateStarted, daysOfWeek);
    }

    public Habit update(String id, String name, String reason, Instant dateStarted, boolean[] daysOfWeek) {
        return mRepository.update(id, name, reason, dateStarted, daysOfWeek);
    }

    public void delete(Habit habit) {
        mRepository.delete(habit);
    }
}
