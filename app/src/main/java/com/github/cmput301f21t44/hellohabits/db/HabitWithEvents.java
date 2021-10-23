package com.github.cmput301f21t44.hellohabits.db;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.model.HabitEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class HabitWithEvents implements Habit {
    @Embedded
    private HabitEntity mHabitEntity;
    @Relation(
            parentColumn = "habit_id",
            entityColumn = "habit_id"
    )
    private List<HabitEventEntity> mHabitEventList;

    public HabitEntity getHabitEntity() {
        return mHabitEntity;
    }

    public HabitWithEvents() {
    }

    public HabitWithEvents(HabitEntity habit) {
        this.mHabitEntity = habit;
    }

    @Override
    public String getId() {
        return mHabitEntity.getId();
    }

    @Override
    public String getTitle() {
        return mHabitEntity.getTitle();
    }

    @Override
    public String getReason() {
        return mHabitEntity.getReason();
    }

    @Override
    public Instant getDateStarted() {
        return mHabitEntity.getDateStarted();
    }

    @Override
    public List<HabitEvent> getEvents() {
        return new ArrayList<>(mHabitEventList);
    }

    public void setHabitEventList(List<HabitEventEntity> mHabitEventList) {
        this.mHabitEventList = mHabitEventList;
    }

    public void setHabitEntity(HabitEntity mHabitEntity) {
        this.mHabitEntity = mHabitEntity;
    }
}
