package com.github.cmput301f21t44.hellohabits.firebase;

import android.annotation.SuppressLint;
import android.util.Log;

import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.habitevent.Location;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Firestore implementation of HabitEvent
 */
public class FSHabitEvent implements HabitEvent, FSDocument {
    public static final String COLLECTION = "events";
    public static final String HABIT_ID = "habitId";
    public static final String DATE = "date";
    public static final String COMMENT = "comment";
    public static final String PHOTO_PATH = "photoPath";
    public static final String LOCATION = "location";

    private final String mId;
    private final String mHabitId;
    private final Instant mDate;

    private String mComment;
    private String mPhotoPath;
    private Location mLocation;

    /**
     * Creates a new FSHabitEvent
     *
     * @param id        UUID of the HabitEvent
     * @param date      Date of when the HabitEvent is denoted
     * @param habitId   UUID of the Habit parent
     * @param comment   Optional comment
     * @param photoPath Optional photo path
     * @param location  Optional location
     */
    public FSHabitEvent(String id, Instant date, String habitId, String comment, String photoPath,
                        Location location) {
        this.mId = id;
        this.mDate = date;
        this.mHabitId = habitId;
        this.mComment = comment;
        this.mPhotoPath = photoPath;
        this.mLocation = location;
    }

    /**
     * Creates a new FSHabitEvent with a generated UUID
     *
     * @param date      Date of when the HabitEvent is denoted
     * @param habitId   UUID of the Habit parent
     * @param comment   Optional comment
     * @param photoPath Optional photo path
     * @param location  Optional location
     */
    public FSHabitEvent(Instant date, String habitId, String comment, String photoPath,
                        Location location) {
        this(UUID.randomUUID().toString(), date, habitId, comment, photoPath, location);
    }

    /**
     * Creates a new FSHabitEvent
     *
     * @param id      UUID of the HabitEvent
     * @param date    Date of when the HabitEvent is denoted
     * @param habitId UUID of the Habit parent
     */
    public FSHabitEvent(String id, Instant date, String habitId) {
        this.mId = id;
        this.mHabitId = habitId;
        this.mDate = date;
    }

    /**
     * Creates a new FSHabitEvent with a comment and generated UUID
     *
     * @param id      UUID of the HabitEvent
     * @param date    Date of when the HabitEvent is denoted
     * @param habitId UUID of the Habit parent
     * @param comment Optional comment
     */
    public FSHabitEvent(String id, Instant date, String habitId, String comment) {
        this(id, date, habitId);
        this.mComment = comment;
    }


    /**
     * Creates a new FSHabitEvent with a comment and generated UUID
     *
     * @param date    Date of when the HabitEvent is denoted
     * @param habitId UUID of the Habit parent
     * @param comment Optional comment
     */
    public FSHabitEvent(Instant date, String habitId, String comment) {
        this(UUID.randomUUID().toString(), date, habitId, comment);
    }

    public FSHabitEvent(QueryDocumentSnapshot doc) {
        this(doc.getId(), FSDocument.instantFromDoc(doc, DATE), doc.getString(HABIT_ID),
                doc.getString(COMMENT), doc.getString(PHOTO_PATH), fromDoc(doc));
    }

    /**
     * TODO: Update for Location and Photo path
     *
     * @param event HabitEvent from which to create a new FSHabitEvent
     */
    public FSHabitEvent(HabitEvent event) {
        this(event.getId(), event.getDate(), event.getHabitId(), event.getComment());
    }

    @SuppressLint("DefaultLocale")
    @SuppressWarnings("ConstantConditions")
    private static Location fromDoc(QueryDocumentSnapshot doc) {
        if (doc.get(LOCATION) != null) {
            double latitude = doc.getDouble(LOCATION + ".latitude");
            double longitude = doc.getDouble(LOCATION + ".longitude");
            double accuracy = doc.getDouble(LOCATION + ".accuracy");
            Log.println(Log.ASSERT, "from Doc", String.format("%f %f %f", latitude, longitude, accuracy));
            return new FSLocation(longitude, latitude, accuracy);
        }

        return null;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public String getHabitId() {
        return mHabitId;
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
        return mLocation;
    }

    /**
     * Converts HabitEvent fields to a Map
     *
     * @return Map of HabitEvent fields
     */
    @Override
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(HABIT_ID, mHabitId);
        map.put(DATE, mDate);
        map.put(COMMENT, mComment);
        map.put(PHOTO_PATH, mPhotoPath);
        map.put(LOCATION, mLocation);
        return map;
    }

    @Override
    public String getKey() {
        return mId;
    }
}
