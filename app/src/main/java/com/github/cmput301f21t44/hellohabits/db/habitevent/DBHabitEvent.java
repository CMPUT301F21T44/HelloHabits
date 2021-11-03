package com.github.cmput301f21t44.hellohabits.db.habitevent;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.github.cmput301f21t44.hellohabits.db.habit.HabitEntity;
import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.Location;

import java.time.Instant;
import java.util.UUID;


/**
 * Immutable model class for HabitEvent
 */
@Entity(tableName = "habit_event",
        foreignKeys = {
                @ForeignKey(onDelete = CASCADE, entity = HabitEntity.class,
                        parentColumns = "habit_id", childColumns = "habit_id")},
        indices = {@Index("habit_id")})
public class DBHabitEvent implements HabitEvent {
    @ColumnInfo(name = "habit_id")
    @NonNull
    private final String mHabitId;
    @ColumnInfo(name = "date_denoted")
    @NonNull
    private final Instant mDate;
    @ColumnInfo(name = "comment")
    private final String mComment;
    @ColumnInfo(name = "photo_path")
    private final String mPhotoPath;
    @Embedded
    private final DBLocation mEmbeddedLocation;
    @PrimaryKey
    @ColumnInfo(name = "habit_event_id")
    @NonNull
    private String mHabitEventId;


    public DBHabitEvent(@NonNull String habitEventId, @NonNull String habitId,
                        @NonNull Instant date, String comment, String photoPath,
                        DBLocation embeddedLocation) {
        this.mHabitEventId = habitEventId;
        this.mHabitId = habitId;
        this.mDate = date;
        this.mComment = comment;
        this.mPhotoPath = photoPath;
        this.mEmbeddedLocation = embeddedLocation;
    }

    public DBHabitEvent(@NonNull String habitEventId, @NonNull String habitId,
                        @NonNull Instant date, String comment, String photoPath,
                        Location location) {
        this(habitEventId, habitId, date, comment, photoPath, DBLocation.from(location));
    }

    public DBHabitEvent(@NonNull String habitId, @NonNull Instant date, String comment,
                        String photoPath, Location location) {
        this(UUID.randomUUID().toString(), habitId, date, comment, photoPath, location);
    }

    public static DBHabitEvent from(HabitEvent habitEvent) {
        return new DBHabitEvent(
                habitEvent.getId(),
                habitEvent.getHabitId(),
                habitEvent.getDate(),
                habitEvent.getComment(),
                habitEvent.getPhotoPath(),
                DBLocation.from(habitEvent.getLocation()));
    }


    @Override
    public String getId() {
        return mHabitEventId;
    }

    @Override
    public Instant getDate() {
        return mDate;
    }

    @Override
    public String getComment() {
        return mComment;
    }

    @Override
    public String getPhotoPath() {
        return mPhotoPath;
    }

    @Override
    public Location getLocation() {
        return mEmbeddedLocation;
    }

    @Override
    public String getHabitId() {
        return mHabitId;
    }

    /**
     * Methods needed by Room
     */

    public String getHabitEventId() {
        return mHabitEventId;
    }

    public void setHabitEventId(String habitEventId) {
        this.mHabitEventId = habitEventId;
    }

    public DBLocation getEmbeddedLocation() {
        return mEmbeddedLocation;
    }
}
