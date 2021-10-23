package com.github.cmput301f21t44.hellohabits.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.db.HabitEntity;
import com.github.cmput301f21t44.hellohabits.db.HabitEntityRepository;
import com.github.cmput301f21t44.hellohabits.model.Habit;

import java.time.Instant;
import java.util.List;

public class HabitViewModel extends AndroidViewModel {
    private final HabitEntityRepository mRepository;
    private final LiveData<List<HabitEntity>> mAllHabits;

    public HabitViewModel(Application application) {
        super(application);
        mRepository = new HabitEntityRepository(application);
        mAllHabits = mRepository.getAllHabits();
    }

    public LiveData<List<HabitEntity>> getAllHabits() {
        return mAllHabits;
    }

    public void insert(String name, String reason, Instant dateStarted) {
        mRepository.insert(name, reason, dateStarted);
    }

    public Habit update(String id, String name, String reason, Instant dateStarted) {
        return mRepository.update(id, name, reason, dateStarted);
    }

    public void delete(Habit habit) {
        /*
          This ViewModel depends on HabitEntity right now, but shouldn't.
          We'll make sure that the repository will be implementation-agnostic in the future.
         */
        mRepository.delete(HabitEntity.from(habit));
    }
}
