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
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TodaysHabitsFragment extends Fragment {
    private FragmentTodaysHabitsBinding binding;
    private HabitViewModel mHabitViewModel;
    private HabitAdapter adapter;
    private NavController mNavController;
    private PreviousListViewModel mPreviousListViewModel;
    private FirebaseAuth mAuth;

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
        binding = FragmentTodaysHabitsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     *
     * @param view
     * @param savedInstanceState
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = NavHostFragment.findNavController(this);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            mNavController.navigate(R.id.loginFragment);
            return;
        }

        ViewModelProvider provider = ViewModelFactory.getProvider(requireActivity());
        mHabitViewModel = provider.get(HabitViewModel.class);
        mPreviousListViewModel = provider.get(PreviousListViewModel.class);
        adapter = HabitAdapter.newInstance((habit) -> {
            mHabitViewModel.select(habit);
            mPreviousListViewModel.select(R.id.TodaysHabitsFragment);
            mNavController.navigate(R.id.action_todaysHabitsFragment_to_viewHabitFragment);
        });
        binding.habitRecyclerView.setAdapter(adapter);
        binding.habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.buttonNewHabit.setOnClickListener(view1 -> {
            mPreviousListViewModel.select(R.id.TodaysHabitsFragment);
            mHabitViewModel.select(null);
            mNavController.navigate(R.id.action_todaysHabitsFragment_to_newHabitFragment);
        });
        binding.viewAllHabits.setOnClickListener(view1 -> {
            mHabitViewModel.select(null);
            mPreviousListViewModel.select(R.id.TodaysHabitsFragment);
            mNavController.navigate(R.id.action_TodaysHabitsFragment_to_allHabitsFragment);
        });
        binding.social.setOnClickListener(v -> {
            mAuth.signOut();
            mNavController.navigate(R.id.loginFragment);
        });
    }

    /**
     * This function list all the habits that should be done today
     */
    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            mNavController.navigate(R.id.loginFragment);
            return;
        }
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
            adapter.submitList(todaysHabits);
        });
    }

    /**
     * This function keeps checking the user while in this page
     * If the user is null,go to login page
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() == null) {
            mNavController.navigate(R.id.loginFragment);
        }
    }

    /**
     * This function close the current function and go to the last page
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}