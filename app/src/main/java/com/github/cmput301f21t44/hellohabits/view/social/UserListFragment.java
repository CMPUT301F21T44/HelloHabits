package com.github.cmput301f21t44.hellohabits.view.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentUserListBinding;
import com.github.cmput301f21t44.hellohabits.databinding.ListUserItemBinding;
import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;
import com.github.cmput301f21t44.hellohabits.model.social.User;
import com.github.cmput301f21t44.hellohabits.view.OnItemClickListener;
import com.github.cmput301f21t44.hellohabits.viewmodel.UserViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

public abstract class UserListFragment extends Fragment {
    protected UserViewModel mUserViewModel;
    protected UserAdapter mAdapter;
    protected FragmentUserListBinding mBinding;
    protected ListUserItemBinding cBinding;

    protected NavController mNavController;


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
     * UserList's onViewCreated lifecycle method
     *
     * @param view               a default view
     * @param savedInstanceState a default Bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = NavHostFragment.findNavController(this);
    }

    /**
     * UserList's onStart lifecycle method
     * <p>
     * Initializes the ViewModels
     */
    @Override
    public void onStart() {
        super.onStart();
        // attach the provider to activity instead of fragment so the fragments can share data
        ViewModelProvider provider = ViewModelFactory.getProvider(requireActivity());
        mUserViewModel = provider.get(UserViewModel.class);
    }

    /**
     * Initializes the RecyclerView's userAdapter
     *
     * @param viewListener onClickListener for Habit List items
     */
    protected void initAdapter(OnItemClickListener<User> viewListener,
                               OnItemClickListener<User> acceptListener,
                               OnItemClickListener<User> rejectListener) {
        mAdapter = UserAdapter.newInstance(viewListener, acceptListener, rejectListener);
        mBinding.userRecyclerView.setAdapter(mAdapter);
        mBinding.userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Show error message as Toast
     *  @param text Text to output
     * @param e    Exception thrown
     * @return
     */
    public CatchFunction showErrorToast(String text, Exception e) {
        String message = text + ": " + e.getLocalizedMessage();
        int duration = message.length() > 20
                ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(requireContext(), message, duration).show();
        return null;
    }


    /**
     * UserList's onDestroyView lifecycle method
     * <p>
     * Unbinds the ViewBinding class from the Fragment
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
        cBinding = null;
    }
}
