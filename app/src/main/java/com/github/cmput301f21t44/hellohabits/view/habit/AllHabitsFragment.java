package com.github.cmput301f21t44.hellohabits.view.habit;

import com.github.cmput301f21t44.hellohabits.R;

/**
 * Fragment for viewing all habits
 */
public class AllHabitsFragment extends HabitListFragment {
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