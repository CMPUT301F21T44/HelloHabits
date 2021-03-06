package com.github.cmput301f21t44.hellohabits.firebase;

import com.github.cmput301f21t44.hellohabits.model.habit.DaysOfWeek;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;
import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEvent;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Firestore implementation of  Habit
 */
public class FSHabit implements Habit, FSDocument {
    public static final String COLLECTION = "habits";
    public static final String TITLE = "title";
    public static final String REASON = "reason";
    public static final String DATE_STARTED = "dateStarted";
    public static final String DAYS_OF_WEEK = "daysOfWeek";
    public static final String IS_PRIVATE = "isPrivate";
    public static final String HABIT_INDEX = "index";

    private final String mId;
    private final String mTitle;
    private final String mReason;
    private final int mIndex;
    private final Instant mDateStarted;
    private final boolean[] mDaysOfWeek;
    private final boolean mPrivate;

    private List<HabitEvent> mHabitEvents;

    /**
     * Create a FSHabit
     *
     * @param id          UUID of the Habit
     * @param title       Title of the Habit
     * @param reason      Reason for starting the Habit
     * @param dateStarted Date on which the Habit was started
     * @param daysOfWeek  Days of the week for when the Habit is scheduled
     * @param isPrivate   Whether the habit is invisible to followers
     * @param index       Index of the Habit in the user's list
     */
    public FSHabit(String id, String title, String reason, Instant dateStarted,
                   boolean[] daysOfWeek, boolean isPrivate, int index) {
        this.mId = id;
        this.mTitle = title;
        this.mReason = reason;
        this.mHabitEvents = new ArrayList<>();
        this.mDateStarted = dateStarted;
        this.mDaysOfWeek = daysOfWeek;
        this.mPrivate = isPrivate;
        this.mIndex = index;
    }

    /**
     * Creates an FSHabit from a QueryDocumentSnapshot
     *
     * @param doc Firestore document
     */
    @SuppressWarnings("unchecked")
    public FSHabit(QueryDocumentSnapshot doc) {
        this(doc.getId(), doc.getString(TITLE), doc.getString(REASON),
                FSDocument.instantFromDoc(doc, DATE_STARTED),
                DaysOfWeek.fromList((List<Boolean>) doc.get(DAYS_OF_WEEK)),
                convertVisibility(doc.getBoolean(IS_PRIVATE)),
                convertIndex(doc.getLong(HABIT_INDEX))
        );
    }

    public FSHabit(Habit habit) {
        this(habit.getId(), habit.getTitle(), habit.getReason(), habit.getDateStarted(),
                habit.getDaysOfWeek(), habit.isPrivate(), habit.getIndex());
    }

    /**
     * Creates a new FSHabit with a generated UUID
     *
     * @param title       Title of the Habit
     * @param reason      Reason for starting the Habit
     * @param dateStarted Date on which the Habit was started
     * @param daysOfWeek  Days of the week for when the Habit is scheduled
     * @param isPrivate   Whether the habit is invisible to followers
     * @param index       Index of the Habit in the user's list
     */
    public FSHabit(String title, String reason, Instant dateStarted, boolean[] daysOfWeek,
                   boolean isPrivate, int index) {
        this(UUID.randomUUID().toString(), title, reason, dateStarted, daysOfWeek, isPrivate, index);
    }

    private static boolean convertVisibility(Boolean bool) {
        return bool != null && bool;
    }

    private static int convertIndex(Long index) {
        return index != null ? Math.toIntExact(index) : 0;
    }

    @Override
    public String getId() {
        return mId;
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

    @Override
    public int getIndex() {
        return mIndex;
    }

    @Override
    public List<HabitEvent> getEvents() {
        return mHabitEvents;
    }

    @Override
    public boolean[] getDaysOfWeek() {
        return mDaysOfWeek;
    }

    @Override
    public boolean isPrivate() {
        return mPrivate;
    }

    /**
     * Set the list of HabitEvents
     *
     * @param habitEvents list of HabitEvents
     */
    public void setHabitEvents(List<HabitEvent> habitEvents) {
        this.mHabitEvents = habitEvents;
    }

    /**
     * Converts Habit fields to a Map
     *
     * @return Map of Habit fields
     */
    @Override
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(TITLE, mTitle);
        map.put(REASON, mReason);
        map.put(DATE_STARTED, mDateStarted);
        map.put(DAYS_OF_WEEK, DaysOfWeek.toList(mDaysOfWeek));
        map.put(IS_PRIVATE, mPrivate);
        map.put(HABIT_INDEX, mIndex);
        return map;
    }

    @Override
    public String getKey() {
        return mId;
    }
}
