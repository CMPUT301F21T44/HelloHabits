package com.github.cmput301f21t44.hellohabits.model.habit;

import com.github.cmput301f21t44.hellohabits.model.HasKey;
import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEvent;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Interface to implement habit functions
 */
public interface Habit extends HasKey {
    /**
     * Check if Habit is scheduled today
     *
     * @param today      Today's date
     * @param daysOfWeek Array of booleans to check from
     * @return true if the Habit is scheduled today, false if not
     */
    static boolean isInDay(ZonedDateTime today, boolean[] daysOfWeek) {
        // dayOfWeek goes from 1-7 (Monday-Sunday)
        int dayOfWeek = today.getDayOfWeek().getValue();

        // daysOfWeek is an array that matches the checkBox for viewing.
        // in case today is Monday:
        // [true, true ,true, true, true, true, true] = every day
        // [true, false , false, false, false, false, false] = only on monday
        // [true, false , false, false, false, false, true] = for monday and sunday
        return daysOfWeek[dayOfWeek - 1];
    }

    /**
     * Check level of consistency
     * <p>
     * if consistency <50% red logo, if between 50 and 75%, yellow logo,
     * if greater than 75% green logo.
     * if habit is non repetitive or hasn't started yet, consistency = 100%
     * <p>
     * This overload gets the list of HabitEvents from the Habit itself
     *
     * @param habit Habit to get consistency of
     * @return Percentage of consistency
     */
    static double getConsistency(Habit habit) {
        return getConsistency(habit, habit.getEvents());
    }

    /**
     * Check level of consistency
     * <p>
     * if consistency <50% red logo, if between 50 and 75%, yellow logo,
     * if greater than 75% green logo.
     * if habit is non repetitive or hasn't started yet, consistency = 100%
     *
     * @param habit  Habit to get consistency of
     * @param events List of habit events to check from
     * @return Percentage of consistency
     */
    static double getConsistency(Habit habit, List<HabitEvent> events) {
        Instant instant = habit.getDateStarted();
        if (instant == null || events == null) return 0;
        LocalDate startDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = LocalDate.now();
        int startDay = startDate.getDayOfWeek().getValue();
        int endDay = endDate.getDayOfWeek().getValue();
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        boolean[] recurrenceDays = habit.getDaysOfWeek();
        int numRecurrenceDays = 0;
        for (boolean recurrenceDay : recurrenceDays) {
            if (recurrenceDay) {
                numRecurrenceDays++;
            }
        }
        long totalDays = 0;
        for (int i = startDay; i <= 7; i++) {
            if (recurrenceDays[i - 1]) {
                totalDays++;
            }
            days--;
        }
        for (int i = endDay; i >= 1; i--) {
            if (recurrenceDays[i - 1]) {
                totalDays++;
            }
            days--;
        }
        totalDays +=  numRecurrenceDays * days / 7;
        return totalDays != 0 ? (double) events.size() / totalDays : 1;
    }

    String getId();

    String getTitle();

    String getReason();

    Instant getDateStarted();

    int getIndex();

    List<HabitEvent> getEvents();

    boolean[] getDaysOfWeek();

    /**
     * @return true if not visible to anyone, false if the habit is visible to followers
     */
    boolean isPrivate();
}
