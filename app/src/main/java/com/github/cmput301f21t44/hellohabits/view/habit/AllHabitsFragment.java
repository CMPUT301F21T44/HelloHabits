package com.github.cmput301f21t44.hellohabits.view.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentAllHabitsBinding;
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class AllHabitsFragment extends Fragment {
    private FragmentAllHabitsBinding binding;
    private HabitViewModel mHabitViewModel;
    private PreviousListViewModel mPreviousListViewModel;
    private HabitAdapter adapter;
    private NavController mNavController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllHabitsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = NavHostFragment.findNavController(this);
        // attach the provider to activity instead of fragment so the fragments can share data
        ViewModelProvider provider = ViewModelFactory.getProvider(requireActivity());
        mHabitViewModel = provider.get(HabitViewModel.class);
        mPreviousListViewModel = provider.get(PreviousListViewModel.class);
        adapter = HabitAdapter.newInstance((habit) -> {
            mHabitViewModel.select(habit);
            mPreviousListViewModel.select(R.id.allHabitsFragment);
            mNavController.navigate(R.id.action_allHabitsFragment_to_viewHabitFragment);
        });
        binding.habitRecyclerView.setAdapter(adapter);
        binding.habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.buttonNewHabit.setOnClickListener(view1 -> {
            mHabitViewModel.select(null);
            mPreviousListViewModel.select(R.id.allHabitsFragment);
            mNavController.navigate(R.id.action_allHabitsFragment_to_newHabitFragment);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mHabitViewModel.getAllHabits().observe(this, habitList -> {
            List<Habit> allHabits = new ArrayList<>(habitList);
            adapter.submitList(allHabits);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        mNavController.navigate(R.id.TodaysHabitsFragment);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}