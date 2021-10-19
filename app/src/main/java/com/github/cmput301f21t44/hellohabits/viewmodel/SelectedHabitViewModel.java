package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.model.Habit;

public class SelectedHabitViewModel extends ViewModel {
    private final MutableLiveData<Habit> selected = new MutableLiveData<>();

    public void select(Habit habit) {
        selected.setValue(habit);
    }

    public LiveData<Habit> getSelected() {
        return selected;
    }
}