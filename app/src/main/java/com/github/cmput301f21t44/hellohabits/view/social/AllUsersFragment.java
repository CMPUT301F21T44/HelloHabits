package com.github.cmput301f21t44.hellohabits.view.social;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.cmput301f21t44.hellohabits.databinding.FragmentUserListBinding;
import com.github.cmput301f21t44.hellohabits.databinding.ListUserItemBinding;
import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;

/**
 * Fragment for viewing all Users
 */
public class AllUsersFragment extends UserListFragment {

    /**
     * UserList's onCreateView lifecycle method
     * <p>
     * Binds the ViewBinding class to the fragment
     *
     * @param inflater           a default LayoutInflater
     * @param container          a default ViewGroup
     * @param savedInstanceState a default Bundle
     * @return a path representing the root component of the corresponding layout
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentUserListBinding.inflate(inflater, container, false);
        cBinding = ListUserItemBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

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
        cBinding.accept.setOnClickListener(view1 -> {
            mUserViewModel.requestFollow(cBinding.userEmail.toString(),
                    changeRequestButton(), showErrorToast("request failed", null));
        });
        mUserViewModel.getAllUsers().observe(this, mAdapter::submitList);
    }

    /**
     * This function changes the request button after request send
     *
     * @return null
     */
    public ThenFunction changeRequestButton() {
        Toast.makeText(requireContext(),"Request send",Toast.LENGTH_LONG).show();
        cBinding.accept.setVisibility(View.INVISIBLE);
        cBinding.reject.setOnClickListener(view -> {
            mUserViewModel.cancelFollowRequest(cBinding.userEmail.toString(),
                    restoreRequestButton(),
                    showErrorToast("cannot cancel request",null));
        });
        return null;
    }

    /**
     * This function changes the request button after request send
     *
     * @return null
     */
    public ThenFunction restoreRequestButton() {
        Toast.makeText(requireContext(),"Request cancelled",Toast.LENGTH_LONG).show();
        cBinding.accept.setVisibility(View.VISIBLE);
        cBinding.accept.setOnClickListener(view -> {
            mUserViewModel.requestFollow(cBinding.userEmail.toString(),
                    changeRequestButton(),
                    showErrorToast("request failed",null ));

        });
        return null;
    }
}


