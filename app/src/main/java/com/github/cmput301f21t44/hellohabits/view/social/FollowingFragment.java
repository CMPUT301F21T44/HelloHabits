package com.github.cmput301f21t44.hellohabits.view.social;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Fragment for viewing followed Users
 */
public class FollowingFragment extends UserListFragment {
    /**
     * FollowingFragment's Lifecycle onViewCreated method
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
     * FollowingFragment's Lifecycle onStart method
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
        mUserViewModel.getFollowing().observe(this, mAdapter::submitList);
    }
}
