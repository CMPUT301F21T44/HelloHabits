package com.github.cmput301f21t44.hellohabits.db.habitevent;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HabitEventDao {
    @Query("SELECT * FROM habit_event WHERE habit_id=:id")
    LiveData<List<HabitEventEntity>> getEventsByHabitId(String id);

    @Insert
    void insert(HabitEventEntity habitEvent);

    @Delete
    void delete(HabitEventEntity habitEvent);

    @Update
    void update(HabitEventEntity habitEvent);
}
