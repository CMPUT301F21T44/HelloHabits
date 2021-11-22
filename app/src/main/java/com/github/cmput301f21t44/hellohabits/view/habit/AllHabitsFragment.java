package com.github.cmput301f21t44.hellohabits.view.habit;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for viewing all habits
 */
public class AllHabitsFragment extends HabitListFragment {
    /**
     * AllHabitsFragment's Lifecycle onViewCreated method
     * <p>
     * Initializes listeners
     *
     * @param view               a default view
     * @param savedInstanceState a default Bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.initListeners(R.id.AllHabitsFragment);
    }

    /**
     * AllHabitsFragment's Lifecycle onStart method
     * <p>
     * Observe all of the user's habits
     */
    @Override
    public void onStart() {
        super.onStart();
        mHabitViewModel.getAllHabits().observe(this, habitList -> {
            List<Habit> allHabits = new ArrayList<>(habitList);
            mAdapter.submitList(allHabits);
        });
    }
}