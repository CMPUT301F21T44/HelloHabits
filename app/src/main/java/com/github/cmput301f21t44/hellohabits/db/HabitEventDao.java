package com.github.cmput301f21t44.hellohabits.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

@Dao
public interface HabitEventDao {
    @Insert
    void insert(HabitEventEntity habitEvent);

    @Delete
    void delete(HabitEventEntity habitEvent);

    @Update
    void update(HabitEventEntity habitEvent);
}
