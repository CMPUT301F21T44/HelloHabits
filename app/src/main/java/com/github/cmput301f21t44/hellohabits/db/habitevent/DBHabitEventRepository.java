package com.github.cmput301f21t44.hellohabits.db.habitevent;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.github.cmput301f21t44.hellohabits.db.AppDatabase;
import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.HabitEventRepository;
import com.github.cmput301f21t44.hellohabits.model.Location;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DBHabitEventRepository implements HabitEventRepository {
    private final HabitEventDao mHabitEventDao;

    public DBHabitEventRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mHabitEventDao = db.habitEventDao();
    }

    public LiveData<List<HabitEvent>> getEventsByHabitId(String habitId) {
        MediatorLiveData<List<HabitEvent>> habitEventList = new MediatorLiveData<>();
        habitEventList.addSource(mHabitEventDao.getEventsByHabitId(habitId), habitEventEntities ->
                habitEventList.setValue(new ArrayList<>(habitEventEntities)));
        return habitEventList;

    }

    @Override
    public void insert(String habitId, String comment) {
        DBHabitEvent newHabitEvent = new DBHabitEvent(habitId, Instant.now(), comment, null,
                null);
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitEventDao.insert(newHabitEvent));
    }

    @Override
    public void delete(HabitEvent habitEvent) {
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitEventDao
                .delete(DBHabitEvent.from(habitEvent)));
    }

    @Override
    public HabitEvent update(String id, String habitId, Instant date, String comment, String photoPath, Location location) {
        DBHabitEvent updatedEvent = new DBHabitEvent(id, habitId, date, comment, photoPath, location);
        AppDatabase.databaseWriteExecutor.execute(() -> mHabitEventDao.update(updatedEvent));
        return updatedEvent;
    }
}
