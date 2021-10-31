package com.github.cmput301f21t44.hellohabits.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.cmput301f21t44.hellohabits.db.habit.HabitEntity;
import com.github.cmput301f21t44.hellohabits.db.habit.HabitEntityRepository;
import com.github.cmput301f21t44.hellohabits.db.habit.HabitWithEvents;
import com.github.cmput301f21t44.hellohabits.model.Habit;

import java.time.Instant;
import java.util.List;

public class HabitViewModel extends AndroidViewModel {
    private final HabitEntityRepository mRepository;
    private final LiveData<List<HabitWithEvents>> mAllHabits;

    private final MutableLiveData<Habit> selected = new MutableLiveData<>();

    public HabitViewModel(Application application) {
        super(application);
        mRepository = new HabitEntityRepository(application);
        mAllHabits = mRepository.getAllHabits();
    }

    public void select(Habit habit) {
        selected.setValue(habit);
    }

    public LiveData<Habit> getSelected() {
        return selected;
    }

    public LiveData<List<HabitWithEvents>> getAllHabits() {
        return mAllHabits;
    }

    public void insert(String name, String reason, Instant dateStarted, boolean[] daysOfWeek) {
        mRepository.insert(name, reason, dateStarted, daysOfWeek);
    }

    public Habit update(String id, String name, String reason, Instant dateStarted, boolean[] daysOfWeek) {
        return mRepository.update(id, name, reason, dateStarted, daysOfWeek);
    }

    public void delete(Habit habit) {
        /*
          This ViewModel depends on HabitEntity right now, but shouldn't.
          We'll make sure that the repository will be implementation-agnostic in the future.
         */
        mRepository.delete(new HabitWithEvents(HabitEntity.from(habit)));
    }
}
