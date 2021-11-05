package com.github.cmput301f21t44.hellohabits.view.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentTodaysHabitsBinding;
import com.github.cmput301f21t44.hellohabits.firebase.Authentication;
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TodaysHabitsFragment extends Fragment {
    private FragmentTodaysHabitsBinding mBinding;
    private HabitViewModel mHabitViewModel;
    private HabitAdapter mAdapter;
    private NavController mNavController;
    private PreviousListViewModel mPreviousListViewModel;
    private Authentication mAuth;

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

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = NavHostFragment.findNavController(this);
        mAuth = new Authentication();
        if (!requireUser()) return;

        ViewModelProvider provider = ViewModelFactory.getProvider(requireActivity());
        mHabitViewModel = provider.get(HabitViewModel.class);
        mPreviousListViewModel = provider.get(PreviousListViewModel.class);
        mAdapter = HabitAdapter.newInstance((habit) -> {
            mHabitViewModel.select(habit);
            mPreviousListViewModel.setDestinationId(R.id.TodaysHabitsFragment);
            mNavController.navigate(R.id.action_todaysHabitsFragment_to_viewHabitFragment);
        });
        mBinding.habitRecyclerView.setAdapter(mAdapter);
        mBinding.habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mBinding.buttonNewHabit.setOnClickListener(view1 -> {
            mPreviousListViewModel.setDestinationId(R.id.TodaysHabitsFragment);
            mHabitViewModel.select(null);
            mNavController.navigate(R.id.action_todaysHabitsFragment_to_newHabitFragment);
        });
        mBinding.viewAllHabits.setOnClickListener(view1 -> {
            mHabitViewModel.select(null);
            mPreviousListViewModel.setDestinationId(R.id.TodaysHabitsFragment);
            mNavController.navigate(R.id.action_TodaysHabitsFragment_to_allHabitsFragment);
        });
        mBinding.social.setOnClickListener(v -> {
            mAuth.signOut();
            mNavController.navigate(R.id.loginFragment);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!requireUser()) return;

        mHabitViewModel.getAllHabits().observe(this, habitList -> {
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
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requireUser();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}