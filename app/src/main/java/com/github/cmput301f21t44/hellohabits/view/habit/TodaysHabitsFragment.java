package com.github.cmput301f21t44.hellohabits.view.habit;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for viewing today's habits
 */
public class TodaysHabitsFragment extends HabitListFragment {
    /**
     * TodaysHabitsFragment's Lifecycle onViewCreated method
     * <p>
     * Initializes listeners
     *
     * @param view               a default view
     * @param savedInstanceState a default Bundle
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.initListeners(R.id.TodaysHabitsFragment);
    }

    /**
     * TodaysHabitsFragment's Lifecycle onStart method
     * <p>
     * Observe all of the user's habits
     */
    @Override
    public void onStart() {
        super.onStart();
        mHabitViewModel.getAllHabits().observe(this, this::onHabitListChanged);
    }

    /**
     * Update the UI to reflect changes in the Habit list
     *
     * @param habitList updated Habit list
     */
    private void onHabitListChanged(List<Habit> habitList) {
        List<Habit> todaysHabits = new ArrayList<>();
        ZonedDateTime today = Instant.now().atZone(ZoneId.systemDefault());
        // traverse all h in habitList, and only masks in those who matches the checkBox
        // checkBox implementation can be seen in isInDay() from Habit.java
        for (Habit h : habitList) {
            if (Habit.isInDay(today, h.getDaysOfWeek())) {
                todaysHabits.add(h);
            }
        }
        mAdapter.submitList(todaysHabits);
    }
}