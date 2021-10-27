package com.github.cmput301f21t44.hellohabits.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.cmput301f21t44.hellohabits.db.habitevent.HabitEventEntity;
import com.github.cmput301f21t44.hellohabits.db.habitevent.HabitEventEntityRepository;
import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.Location;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class HabitEventViewModel extends AndroidViewModel {
    private final HabitEventEntityRepository mRepository;
    private final MutableLiveData<HabitEvent> selected = new MutableLiveData<>();

    public HabitEventViewModel(Application application) {
        super(application);
        mRepository = new HabitEventEntityRepository(application);
    }

    public LiveData<List<HabitEvent>> getHabitEventsById(String habitId) {
        MediatorLiveData<List<HabitEvent>> habitEvents = new MediatorLiveData<>();
        habitEvents.addSource(mRepository.getEventsByHabitId(habitId), v -> {
            List<HabitEvent> list = new ArrayList<>(v);
            habitEvents.setValue(list);
        });

        return habitEvents;
    }

    public void insert(String habitId, String comment) {
        mRepository.insert(habitId, comment);
    }

    public HabitEvent update(String id, String habitId, Instant date, String comment, String photoPath, Location location) {
        return mRepository.update(id,habitId, date, comment, photoPath, location);
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
