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
public class FSHabitEvent implements HabitEvent {
    public static final String COLLECTION = "events";
    public static final String HABIT_ID = "habitId";
    public static final String DATE = "date";
    public static final String COMMENT = "comment";

    private final String id;
    private final String habitId;
    private final Instant date;

    private String comment;
    private String photoPath;
    private Location location;

    /**
     * Creates a new FSHabitEvent
     *
     * @param habitId         UUID of the Habit parent
     * @param comment         Optional comment
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    public FSHabitEvent(String id, Instant date, String habitId) {
        this.id = id;
        this.habitId = habitId;
        this.date = date;
    }

    /**
     * Creates a new FSHabitEvent with a comment and generated UUID
     *
     * @param id UUID of the HabitEvent
     * @param date    Date of when the HabitEvent is denoted
     * @param habitId UUID of the Habit parent
     * @param comment Optional comment
     */
    public FSHabitEvent(String id, Instant date, String habitId, String comment) {
        this(id, date, habitId);
        this.comment = comment;
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

    /**
     * Creates an FSHabitEvent instance from a DocumentSnapshot
     * @param doc Firestore document
     * @return FSHabitEvent from the document
     */
    public static FSHabitEvent fromSnapshot(QueryDocumentSnapshot doc) {
        String id = doc.getId();
        String habitId = doc.getString(HABIT_ID);
        Instant date = FirestoreRepository.instantFromDoc(doc, DATE);
        String comment = doc.getString(COMMENT);
        return new FSHabitEvent(id, date, habitId, comment);
    }

    /**
     * Converts HabitEvent fields to a Map
     * @param event HabitEvent to convert
     * @return Map of HabitEvent fields
     */
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
