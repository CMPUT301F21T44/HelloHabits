package com.github.cmput301f21t44.hellohabits.view.habit;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.github.cmput301f21t44.hellohabits.R;

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
    }

    /**
     * AllHabitsFragment's Lifecycle onStart method
     * <p>
     * Observe all of the user's habits
     */
    @Override
    public void onStart() {
        super.onStart();
        super.initListeners(R.id.AllHabitsFragment, () -> mHabitViewModel.getAllHabits());
    }
}