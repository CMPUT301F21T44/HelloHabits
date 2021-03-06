package com.github.cmput301f21t44.hellohabits.view.habit;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.github.cmput301f21t44.hellohabits.viewmodel.UserViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.util.Objects;

public class UserHabitListFragment extends HabitListFragment {
    private UserViewModel mUserViewModel;

    /**
     * UserHabitListFragment's Lifecycle onViewCreated method
     * <p>
     * Initializes listeners
     *
     * @param view               a default view
     * @param savedInstanceState a default Bundle
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserViewModel = ViewModelFactory.getProvider(requireActivity()).get(UserViewModel.class);
    }

    /**
     * UserHabitListFragment's Lifecycle onStart method
     * <p>
     * Observe the selected user's public habits
     */
    @Override
    public void onStart() {
        super.onStart();
        initAdapter(() ->
                        mHabitViewModel.getUserPublicHabits(
                                Objects.requireNonNull(
                                        mUserViewModel.getSelectedUser().getValue()).getEmail()),
                (habit) -> {
                    // do nothing, the user should not be able to view other details
                    // about the other user's habits
                });
    }
}
