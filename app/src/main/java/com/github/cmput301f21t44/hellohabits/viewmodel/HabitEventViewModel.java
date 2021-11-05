package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.firebase.FirebaseTask;
import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.HabitEventRepository;

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

    public void insert(String habitId, String comment, FirebaseTask.ThenFunction successCallback,
                       FirebaseTask.CatchFunction failCallback) {
        mRepository.insert(habitId, comment, successCallback, failCallback);
    }

    public void update(String id, String habitId, Instant date, String comment,
                       FirebaseTask.ResultFunction<HabitEvent> successCallback,
                       FirebaseTask.CatchFunction failCallback) {
        mRepository.update(id, habitId, date, comment, successCallback, failCallback);
    }

    public void delete(HabitEvent habitEvent, FirebaseTask.CatchFunction failCallback) {
        mRepository.delete(habitEvent, failCallback);
    }

    public void select(HabitEvent habit) {
        selected.setValue(habit);
    }

    public LiveData<HabitEvent> getSelected() {
        return selected;
    }
}
