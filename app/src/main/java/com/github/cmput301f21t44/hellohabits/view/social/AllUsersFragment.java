package com.github.cmput301f21t44.hellohabits.view.social;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Fragment for viewing all Users
 */
public class AllUsersFragment extends UserListFragment {
    /**
     * AllUsersFragment's Lifecycle onViewCreated method
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
     * AllUsersFragment's Lifecycle onStart method
     * <p>
     * Observe all of the user's habits
     */
    @Override
    public void onStart() {
        super.onStart();
        initAdapter(item -> {
        }, item -> {
        }, item -> {
        });
        // unfollow / follow
        mUserViewModel.getAllUsers().observe(this, mAdapter::submitList);
    }
}
