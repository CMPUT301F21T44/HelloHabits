package com.github.cmput301f21t44.hellohabits.view.social;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Fragment for viewing all followers
 */
public class FollowersFragment extends UserListFragment {
    /**
     * FollowersFragment's Lifecycle onViewCreated method
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
     * FollowersFragment's Lifecycle onStart method
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
        // follower status = REQUESTED / ACCEPTED
        // remove follower
        // following status = null / REQUESTED / ACCEPTED
        mUserViewModel.getFollowers().observe(this, mAdapter::submitList);
    }
}
