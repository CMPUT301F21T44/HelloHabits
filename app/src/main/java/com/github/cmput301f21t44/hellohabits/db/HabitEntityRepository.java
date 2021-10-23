package com.github.cmput301f21t44.hellohabits.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.model.HabitRepository;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class HabitEntityRepository implements HabitRepository<HabitWithEvents> {
    private final HabitDao mHabitDao;
    private final LiveData<List<HabitWithEvents>> mAllHabits;

    public HabitEntityRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mHabitDao = db.habitDao();
        mAllHabits = mHabitDao.getAllHabits();
    }

    public LiveData<List<HabitWithEvents>> getAllHabits() {
        return mAllHabits;
    }

    @Override
    public void insert(String title, String reason, Instant dateStarted) {
        HabitEntity habit = new HabitEntity(title, reason, dateStarted);
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitDao.insert(habit));
    }

    @Override
    public void delete(HabitWithEvents habit) {
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitDao.delete(habit.getHabitEntity()));
    }


    @Override
    public HabitWithEvents update(String id, String title, String reason, Instant dateStarted) {
        HabitEntity updatedEntity = new HabitEntity(id, title, reason, dateStarted);
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitDao.update(updatedEntity));
        for (HabitWithEvents h : Objects.requireNonNull(mAllHabits.getValue())) {
            if (h.getId().equals(updatedEntity.getId())) {
                return h;
            }
        }

        // shouldn't get here
        return null;
    }
}
