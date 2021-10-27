package com.github.cmput301f21t44.hellohabits.db.habit;

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
public class HabitEntity {
    /**
     * All fields should be final except for the primary key, because the
     * Room decorator functions need to be able to modify the id for some reason.
     */
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "habit_id")
    private String mHabitId;

    @ColumnInfo(name = "title")
    @NonNull
    private final String mTitle;

    @ColumnInfo(name = "reason")
    @NonNull
    private final String mReason;

    @ColumnInfo(name = "date_started")
    @NonNull
    private final Instant mDateStarted;

    @ColumnInfo(name = "days_of_week")
    private final boolean[] mDaysOfWeek;

    public HabitEntity(@NonNull String id, @NonNull String title, @NonNull String reason,
                       @NonNull Instant dateStarted, @NonNull boolean[] daysOfWeek) {
        this.mHabitId = id;
        this.mTitle = title;
        this.mReason = reason;
        this.mDateStarted = dateStarted;
        this.mDaysOfWeek = daysOfWeek;
    }

    public HabitEntity(String title, String reason, Instant dateStarted, boolean[] daysOfWeek) {
        this(UUID.randomUUID().toString(), title, reason, dateStarted, daysOfWeek);
    }

    public String getHabitId() {
        return mHabitId;
    }

    public void setHabitId(@NonNull String hid) {
        this.mHabitId = hid;
    }

    @NonNull
    public String getId() {
        return mHabitId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getReason() {
        return mReason;
    }

    public Instant getDateStarted() {
        return mDateStarted;
    }

    public boolean[] getDaysOfWeek() {
        return mDaysOfWeek;
    }

    public static HabitEntity from(Habit habit) {
        return new HabitEntity(habit.getId(), habit.getTitle(), habit.getReason(),
                habit.getDateStarted(), habit.getDaysOfWeek());
    }
}
