package com.github.cmput301f21t44.hellohabits.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.model.HabitRepository;

import java.time.Instant;
import java.util.List;

public class HabitEntityRepository implements HabitRepository<HabitEntity> {
    private final HabitDao mHabitDao;
    private final LiveData<List<HabitEntity>> mAllHabits;

    public HabitEntityRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mHabitDao = db.habitDao();
        mAllHabits = mHabitDao.getAllHabits();
    }

    public LiveData<List<HabitEntity>> getAllHabits() {
        return mAllHabits;
    }

    @Override
    public void insert(String title, String reason, Instant dateStarted) {
        HabitEntity habit = new HabitEntity(title, reason, dateStarted);
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitDao.insert(habit));
    }

    @Override
    public void delete(HabitEntity habit) {
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitDao.delete(habit));
    }

}
