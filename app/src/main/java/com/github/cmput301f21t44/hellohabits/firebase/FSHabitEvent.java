package com.github.cmput301f21t44.hellohabits.firebase;

import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.model.Location;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Firestore implementation of HabitEvent
 */
public class FSHabitEvent implements HabitEvent, FSDocument<FSHabitEvent> {
    public static final String COLLECTION = "events";
    public static final String HABIT_ID = "habitId";
    public static final String DATE = "date";
    public static final String COMMENT = "comment";

    private final String mId;
    private final String mHabitId;
    private final Instant mDate;

    private String mComment;
    private String photoPath;
    private Location location;

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
        this(doc.getId(), FirestoreRepository.instantFromDoc(doc, DATE), doc.getString(HABIT_ID),
                doc.getString(COMMENT));
    }

    /**
     * TODO: Update for Location and Photo path
     *
     * @param event HabitEvent from which to create a new FSHabitEvent
     */
    public FSHabitEvent(HabitEvent event) {
        this(event.getId(), event.getDate(), event.getHabitId(), event.getComment());
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
        return photoPath;
    }

    @Override
    public Location getLocation() {
        return location;
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
        return map;
    }

    @Override
    public String getKey() {
        return mId;
    }
}
