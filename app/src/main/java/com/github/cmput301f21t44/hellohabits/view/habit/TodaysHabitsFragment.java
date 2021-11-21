package com.github.cmput301f21t44.hellohabits.view.habit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentTodaysHabitsBinding;
import com.github.cmput301f21t44.hellohabits.firebase.Authentication;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;
import com.github.cmput301f21t44.hellohabits.view.MainActivity;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for viewing today's habits
 */
public class TodaysHabitsFragment extends Fragment {
    private FragmentTodaysHabitsBinding mBinding;
    private HabitViewModel mHabitViewModel;
    private HabitAdapter mAdapter;
    private NavController mNavController;
    private PreviousListViewModel mPreviousListViewModel;
    private Authentication mAuth;

    /**
     * When the view is created, connect the layout to the class using binding
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
        mBinding = FragmentTodaysHabitsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    /**
     * Navigates to login if there is no user
     *
     * @return true if there's a user, false if not
     */
    private boolean requireUser() {
        if (mAuth.getCurrentUser() == null) {
            mNavController.navigate(R.id.loginFragment);
            return false;
        }
        return true;
    }

    /**
     * TodaysHabitsFragment's Lifecycle onViewCreated method
     * <p>
     * Initializes member variables and button OnClickListeners
     *
     * @param view               a default view
     * @param savedInstanceState a default Bundle
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = NavHostFragment.findNavController(this);
        mAuth = new Authentication();
        if (!requireUser()) return;

        ViewModelProvider provider = ViewModelFactory.getProvider(requireActivity());
        mHabitViewModel = provider.get(HabitViewModel.class);
        mPreviousListViewModel = provider.get(PreviousListViewModel.class);
        mAdapter = HabitAdapter.newInstance((habit) -> {
            mHabitViewModel.setSelectedHabit(habit);
            mPreviousListViewModel.setDestinationId(R.id.TodaysHabitsFragment);
            mNavController.navigate(R.id.ViewHabitFragment);
        });
        mBinding.habitRecyclerView.setAdapter(mAdapter);
        mBinding.habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mBinding.buttonNewHabit.setOnClickListener(view1 -> {
            mPreviousListViewModel.setDestinationId(R.id.TodaysHabitsFragment);
            mHabitViewModel.setSelectedHabit(null);
            mNavController.navigate(R.id.HabitCreateEditFragment);
        });
        mBinding.viewAllHabits.setOnClickListener(view1 -> {
            mHabitViewModel.setSelectedHabit(null);
            mPreviousListViewModel.setDestinationId(R.id.TodaysHabitsFragment);
            mNavController.navigate(R.id.AllHabitsFragment);
        });
        mBinding.social.setOnClickListener(v -> showSignOutDialog());
    }

    /**
     * Show an AlertDialog when trying to sign out
     */
    private void showSignOutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Sign out")
                .setMessage("Are you sure you want to sign out?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YES", (dialog, b) -> signOut())
                .setNegativeButton("NO", null).show();

    }

    /**
     * Signs out and restarts MainActivity to clear all LiveData listeners
     */
    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        requireActivity().startActivity(intent);
    }

    /**
     * TodaysHabitsFragment's Lifecycle onStart method
     */
    @Override
    public void onStart() {
        super.onStart();
        if (!requireUser()) return;

        mHabitViewModel.getAllHabits().observe(this, this::onHabitListChanged);
    }

    /**
     * This function keeps checking the user while in this page
     * If the user is null,go to login page
     */
    @Override
    public void onResume() {
        super.onResume();
        requireUser();
    }

    /**
     * This function close the current function and go to the last page
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    /**
     * Update the UI to reflect changes in the Habit list
     *
     * @param habitList updated  Habit list
     */
    private void onHabitListChanged(List<Habit> habitList) {
        List<Habit> todaysHabits = new ArrayList<>();
        ZonedDateTime today = Instant.now().atZone(ZoneId.systemDefault());
        // traverse all h in habitList, and only masks in those who matches the checkBox
        // checkBox implementation can be seen in isInDay() from Habit.java
        for (Habit h : habitList) {
            if (Habit.isInDay(today, h.getDaysOfWeek())) {
                todaysHabits.add(h);
            }
        }
        mAdapter.submitList(todaysHabits);
    }
}