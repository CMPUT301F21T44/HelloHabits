package com.github.cmput301f21t44.hellohabits.view.social;

/**
 * Fragment for viewing followed Users
 */
public class FollowingFragment extends UserListFragment {
    /**
     * FollowingFragment's Lifecycle onStart method
     * <p>
     * Observe all of the user's habits
     */
    @Override
    public void onStart() {
        super.onStart();
        mUserViewModel.getFollowing().observe(this, mAdapter::submitList);
    }
}
