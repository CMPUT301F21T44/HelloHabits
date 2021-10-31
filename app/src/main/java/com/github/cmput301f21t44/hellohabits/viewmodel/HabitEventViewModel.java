package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.HabitEventRepository;
import com.github.cmput301f21t44.hellohabits.model.Location;

import java.time.Instant;
import java.util.List;

public class HabitEventViewModel extends ViewModel {
    private final HabitEventRepository mRepository;
    private final MutableLiveData<HabitEvent> selected = new MutableLiveData<>();

    public HabitEventViewModel(HabitEventRepository habitEventRepository) {
        mRepository = habitEventRepository;
    }

    public LiveData<List<HabitEvent>> getHabitEventsById(String habitId) {
        return mRepository.getEventsByHabitId(habitId);
    }

    public void insert(String habitId, String comment) {
        mRepository.insert(habitId, comment);
    }

    public HabitEvent update(String id, String habitId, Instant date, String comment, String photoPath, Location location) {
        return mRepository.update(id, habitId, date, comment, photoPath, location);
    }

    public void delete(HabitEvent habitEvent) {
        mRepository.delete(habitEvent);
    }

    public void select(HabitEvent habit) {
        selected.setValue(habit);
    }

    public LiveData<HabitEvent> getSelected() {
        return selected;
    }
}
