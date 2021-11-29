package com.github.cmput301f21t44.hellohabits.view.habit;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.view.MainActivity;
import com.github.cmput301f21t44.hellohabits.viewmodel.UserViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.time.Instant;

/**
 * Fragment for viewing today's habits
 */
public class TodaysHabitsFragment extends HabitListFragment {
    /**
     * TodaysHabitsFragment's Lifecycle onStart method
     * <p>
     * Observe all of the user's habits
     */
    @Override
    public void onStart() {
        super.onStart();
        super.initListeners(R.id.TodaysHabitsFragment,
                () -> mHabitViewModel.getTodaysHabits(Instant.now()));
    }

    @Override
    public void onResume() {
        super.onResume();
        setHeader();
    }

    /**
     * Sets the navigation header message
     */
    private void setHeader() {
        // kinda hacky
        MainActivity mainActivity = (MainActivity) requireActivity();
        UserViewModel mUserViewModel = ViewModelFactory.getProvider(mainActivity)
                .get(UserViewModel.class);
        mUserViewModel.getCurrentUser().removeObservers(mainActivity);
        mUserViewModel.getCurrentUser().observe(mainActivity, user ->
                mainActivity.setHeaderMessage(user.getName() != null ?
                        String.format("Hello, %s", user.getName()) : ""));
    }
}
