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
    public static final String DATE_STARTED_EPOCH = "dateStarted.epochSecond";
    public static final String DATE_STARTED_NANO = "dateStarted.nano";
    public static final String DAYS_OF_WEEK = "daysOfWeek";
    private final String id;
    private final String title;
    private final String reason;
    private final Instant dateStarted;
    private final boolean[] daysOfWeek;

    private List<HabitEvent> habitEvents;

    public FSHabit(String id, String title, String reason, Instant dateStarted,
                   boolean[] daysOfWeek) {
        this.id = id;
        this.title = title;
        this.reason = reason;
        this.habitEvents = new ArrayList<>();
        this.dateStarted = dateStarted;
        this.daysOfWeek = daysOfWeek;
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
        Long epochSeconds = doc.getLong(DATE_STARTED_EPOCH);
        Long nanoAdjustment = doc.getLong(DATE_STARTED_NANO);
        Instant dateStarted = (epochSeconds != null && nanoAdjustment != null) ?
                Instant.ofEpochSecond(epochSeconds, nanoAdjustment) : null;
        List<Boolean> dayList = (List<Boolean>) doc.get(DAYS_OF_WEEK);
        return new FSHabit(id, title, reason, dateStarted, getDaysOfWeek(dayList));
    }

    public static Map<String, Object> getMap(FSHabit habit) {
        Map<String, Object> map = new HashMap<>();
        map.put(TITLE, habit.title);
        map.put(REASON, habit.reason);
        map.put(DATE_STARTED, habit.dateStarted);
        List<Boolean> days = new ArrayList<>();
        for (boolean b : habit.daysOfWeek) {
            days.add(b);
        }
        map.put(DAYS_OF_WEEK, days);
        return map;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public Instant getDateStarted() {
        return dateStarted;
    }

    @Override
    public List<HabitEvent> getEvents() {
        return habitEvents;
    }

    @Override
    public boolean[] getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setHabitEvents(List<HabitEvent> habitEvents) {
        this.habitEvents = habitEvents;
    }
}
