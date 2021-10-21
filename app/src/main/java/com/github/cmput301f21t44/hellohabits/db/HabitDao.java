package com.github.cmput301f21t44.hellohabits.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.cmput301f21t44.hellohabits.model.Habit;

import java.util.List;

@Dao
public interface HabitDao {
    @Query("SELECT * FROM habits")
    LiveData<List<HabitEntity>> getAllHabits();

    @Insert
    void insert(HabitEntity habit);

    @Query("DELETE FROM habits")
    void deleteAll();

    @Delete
    void delete(HabitEntity habit);

}
