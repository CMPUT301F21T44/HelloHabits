package com.github.cmput301f21t44.hellohabits.view.social;

/**
 * Fragment for viewing all followers
 */
public class FollowersFragment extends UserListFragment {
    /**
     * FollowersFragment's Lifecycle onStart method
     * <p>
     * Observe all of the user's habits
     */
    @Override
    public void onStart() {
        super.onStart();
        mUserViewModel.getFollowers().observe(this, mAdapter::submitList);
    }
}
