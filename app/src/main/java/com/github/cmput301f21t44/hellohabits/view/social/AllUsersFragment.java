package com.github.cmput301f21t44.hellohabits.view.social;

/**
 * Fragment for viewing all Users
 */
public class AllUsersFragment extends UserListFragment {
    /**
     * AllUsersFragment's Lifecycle onStart method
     * <p>
     * Observe all of the user's habits
     */
    @Override
    public void onStart() {
        super.onStart();
        mUserViewModel.getAllUsers().observe(this, mAdapter::submitList);
    }
}


