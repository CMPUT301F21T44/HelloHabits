package com.github.cmput301f21t44.hellohabits.firebase;

import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FSHabit implements Habit {
    public static final String COLLECTION = "habits";
    public static final String TITLE = "title";
    public static final String REASON = "reason";
    public static final String DATE_STARTED = "dateStarted";
    public static final String DAYS_OF_WEEK = "daysOfWeek";

    private final String mId;
    private final String mTitle;
    private final String mReason;
    private final Instant mDateStarted;
    private final boolean[] mDaysOfWeek;

    private List<HabitEvent> mHabitEvents;

    public FSHabit(String id, String title, String reason, Instant dateStarted,
                   boolean[] daysOfWeek) {
        this.mId = id;
        this.mTitle = title;
        this.mReason = reason;
        this.mHabitEvents = new ArrayList<>();
        this.mDateStarted = dateStarted;
        this.mDaysOfWeek = daysOfWeek;
    }

    public FSHabit(String title, String reason, Instant dateStarted, boolean[] daysOfWeek) {
        this(UUID.randomUUID().toString(), title, reason, dateStarted, daysOfWeek);
    }

    private static boolean[] getDaysOfWeek(List<Boolean> dayList) {
        boolean[] daysOfWeek = new boolean[7];
        assert dayList != null;
        for (int i = 0; i < 7; ++i) {
            daysOfWeek[i] = dayList.get(i);
        }
        return daysOfWeek;
    }

    public static FSHabit fromSnapshot(QueryDocumentSnapshot doc) {
        String id = doc.getId();
        String title = doc.getString(TITLE);
        String reason = doc.getString(REASON);
        Instant dateStarted = FirestoreRepository.instantFromDoc(doc, DATE_STARTED);
        List<Boolean> dayList = (List<Boolean>) doc.get(DAYS_OF_WEEK);
        return new FSHabit(id, title, reason, dateStarted, getDaysOfWeek(dayList));
    }

    public static Map<String, Object> getMap(FSHabit habit) {
        Map<String, Object> map = new HashMap<>();
        map.put(TITLE, habit.mTitle);
        map.put(REASON, habit.mReason);
        map.put(DATE_STARTED, habit.mDateStarted);
        List<Boolean> days = new ArrayList<>();
        for (boolean b : habit.mDaysOfWeek) {
            days.add(b);
        }
        map.put(DAYS_OF_WEEK, days);
        return map;
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
    public List<HabitEvent> getEvents() {
        return mHabitEvents;
    }

    @Override
    public boolean[] getDaysOfWeek() {
        return mDaysOfWeek;
    }

    public void setHabitEvents(List<HabitEvent> habitEvents) {
        this.mHabitEvents = habitEvents;
    }
}
