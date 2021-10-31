package com.github.cmput301f21t44.hellohabits.db.habitevent;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.db.AppDatabase;
import com.github.cmput301f21t44.hellohabits.model.HabitEventRepository;
import com.github.cmput301f21t44.hellohabits.model.Location;

import java.time.Instant;
import java.util.List;

public class HabitEventEntityRepository implements HabitEventRepository<HabitEventEntity> {
    private final HabitEventDao mHabitEventDao;

    public HabitEventEntityRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mHabitEventDao = db.habitEventDao();
    }

    public LiveData<List<HabitEventEntity>> getEventsByHabitId(String habitId) {
        return mHabitEventDao.getEventsByHabitId(habitId);
    }

    @Override
    public void insert(String habitId, String comment) {
        HabitEventEntity habitEvent = new HabitEventEntity(habitId, Instant.now(), comment, null,
                null);
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitEventDao.insert(habitEvent));
    }

    @Override
    public void delete(HabitEventEntity habitEvent) {
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitEventDao.delete(habitEvent));
    }

    @Override
    public HabitEventEntity update(String id, String habitId, Instant date, String comment, String photoPath, Location location) {
        HabitEventEntity updatedEvent = new HabitEventEntity(id, habitId, date, comment, photoPath, location);
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitEventDao.update(updatedEvent));
        return updatedEvent;
    }
}
