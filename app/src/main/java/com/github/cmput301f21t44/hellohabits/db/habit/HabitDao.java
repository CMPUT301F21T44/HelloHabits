package com.github.cmput301f21t44.hellohabits.db.habit;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HabitDao {
    @Transaction
    @Query("SELECT * FROM habits")
    LiveData<List<DBHabit>> getAllHabits();

    @Insert
    void insert(HabitEntity habit);

    @Query("DELETE FROM habits")
    void deleteAll();

    @Delete
    void delete(HabitEntity habit);

    @Update
    void update(HabitEntity habit);
}
