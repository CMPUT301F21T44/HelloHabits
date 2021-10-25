package com.github.cmput301f21t44.hellohabits.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.cmput301f21t44.hellohabits.db.HabitEventEntity;
import com.github.cmput301f21t44.hellohabits.db.HabitEventEntityRepository;
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.Location;

import java.time.Instant;

public class HabitEventViewModel extends AndroidViewModel {
    private final HabitEventEntityRepository mRepository;
    private final MutableLiveData<HabitEvent> selected = new MutableLiveData<>();

    public HabitEventViewModel(Application application) {
        super(application);
        mRepository = new HabitEventEntityRepository(application);
    }

    public void insert(String habitId, String comment) {
        mRepository.insert(habitId, comment);
    }

    public HabitEvent update(String id, Instant date, String comment, String photoPath, Location location) {
        return mRepository.update(id, date, comment, photoPath, location);
    }

    public void delete(HabitEvent habitEvent) {
        mRepository.delete(HabitEventEntity.from(habitEvent));
    }

    public void select(HabitEvent habit) {
        selected.setValue(habit);
    }

    public LiveData<HabitEvent> getSelected() {
        return selected;
    }
}
