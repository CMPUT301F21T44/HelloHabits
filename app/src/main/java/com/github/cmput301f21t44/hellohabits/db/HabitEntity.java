package com.github.cmput301f21t44.hellohabits.db;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.github.cmput301f21t44.hellohabits.model.Habit;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable model class for a Habit
 */
@Entity(tableName = "habits")
public class HabitEntity implements Habit {
    @PrimaryKey
    @NonNull
    private String hid;

    @ColumnInfo(name = "title")
    private final String mTitle;

    @ColumnInfo(name = "reason")
    private final String mReason;

    @ColumnInfo(name = "date_started")
    private final Instant mDateStarted;

    public HabitEntity(@NonNull String id, String title, String reason, Instant dateStarted) {
        this.hid = id;
        this.mTitle = title;
        this.mReason = reason;
        this.mDateStarted = dateStarted;
    }

    public HabitEntity(String title, String reason, Instant dateStarted) {
        this.hid = UUID.randomUUID().toString();
        this.mTitle = title;
        this.mReason = reason;
        this.mDateStarted = dateStarted;
    }

    @NonNull
    public String getHid() {
        return hid;
    }

    public void setHid(@NonNull String hid) {
        this.hid = hid;
    }

    @Override
    public String getId() {
        return hid;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getReason() {
        return mReason;
    }

    @Override
    public Instant getDateStarted() {
        return mDateStarted;
    }
}
