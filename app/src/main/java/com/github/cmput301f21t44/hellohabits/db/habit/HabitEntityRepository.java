package com.github.cmput301f21t44.hellohabits.db.habit;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.github.cmput301f21t44.hellohabits.db.AppDatabase;
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.model.HabitRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class HabitEntityRepository implements HabitRepository {
    private final HabitDao mHabitDao;
    private final MediatorLiveData<List<Habit>> mAllHabits;

    public HabitEntityRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mHabitDao = db.habitDao();
        mAllHabits = new MediatorLiveData<>();
        mAllHabits.addSource(mHabitDao.getAllHabits(), habitList ->
                mAllHabits.setValue(new ArrayList<>(habitList)));
    }

    public LiveData<List<Habit>> getAllHabits() {
        return mAllHabits;
    }

    @Override
    public void insert(String title, String reason, Instant dateStarted, boolean[] daysOfWeek) {
        HabitEntity newHabit = new HabitEntity(title, reason, dateStarted, daysOfWeek);
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitDao.insert(newHabit));
    }

    @Override
    public void delete(Habit habit) {
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitDao.delete(HabitEntity.from(habit)));
    }


    @Override
    public Habit update(String id, String title, String reason, Instant dateStarted,
                        boolean[] daysOfWeek) {
        HabitEntity updatedEntity = new HabitEntity(id, title, reason, dateStarted, daysOfWeek);
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitDao.update(updatedEntity));

        return new HabitWithEvents(updatedEntity);
    }
}
