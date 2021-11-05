package com.github.cmput301f21t44.hellohabits.firebase;

import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.Location;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FSHabitEvent implements HabitEvent {
    public static final String COLLECTION = "events";
    public static final String HABIT_ID = "habitId";
    public static final String DATE = "date";
    public static final String DATE_EPOCH = "date.epochSecond";
    public static final String DATE_NANO = "date.nano";
    public static final String COMMENT = "comment";
    public static final String PHOTO_PATH = "photoPath";
    public static final String LOCATION = "location";

    private final String id;
    private final String habitId;
    private final Instant date;

    private String comment;
    private String photoPath;
    private Location location;

    public FSHabitEvent(String id, Instant date, String habitId) {
        this.id = id;
        this.habitId = habitId;
        this.date = date;
    }

    public FSHabitEvent(String id, Instant date, String habitId, String comment) {
        this(id, date, habitId);
        this.comment = comment;
    }

    public FSHabitEvent(Instant date, String habitId, String comment) {
        this(UUID.randomUUID().toString(), date, habitId, comment);
    }

    public FSHabitEvent(String id, Instant date, String habitId, String comment, String photoPath,
                        Location location) {
        this(id, date, habitId, comment);
        this.photoPath = photoPath;
        this.location = location;
    }

    public static FSHabitEvent fromSnapshot(QueryDocumentSnapshot doc) {
        String id = doc.getId();
        String habitId = doc.getString(HABIT_ID);
        Long epochSecond = doc.getLong(DATE_EPOCH);
        Long nanoAdjustment = doc.getLong(DATE_NANO);
        Instant date = (epochSecond != null && nanoAdjustment != null)
                ? Instant.ofEpochSecond(epochSecond, nanoAdjustment) : null;
        String comment = doc.getString(COMMENT);
        return new FSHabitEvent(id, date, habitId, comment);
    }

    public static Map<String, Object> getMap(FSHabitEvent event) {
        Map<String, Object> map = new HashMap<>();
        map.put(HABIT_ID, event.habitId);
        map.put(DATE, event.date);
        map.put(COMMENT, event.comment);
        return map;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getHabitId() {
        return habitId;
    }

    @Override
    public Instant getDate() {
        return date;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public String getPhotoPath() {
        return photoPath;
    }

    @Override
    public Location getLocation() {
        return location;
    }

}
