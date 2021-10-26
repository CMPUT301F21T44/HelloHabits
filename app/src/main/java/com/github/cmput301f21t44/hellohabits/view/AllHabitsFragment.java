package com.github.cmput301f21t44.hellohabits.view;

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
import com.github.cmput301f21t44.hellohabits.databinding.FragmentAllHabitsBinding;
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;

import java.util.ArrayList;
import java.util.List;

public class AllHabitsFragment extends Fragment implements OnItemClickListener<Habit>{
    private FragmentAllHabitsBinding binding;
    private HabitViewModel mHabitViewModel;
    private HabitAdapter adapter;
    private NavController mNavController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllHabitsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = NavHostFragment.findNavController(this);
        // attach the provider to activity instead of fragment so the fragments can share data
        mHabitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        adapter = new HabitAdapter(new HabitAdapter.HabitDiff(), this);
        binding.habitRecyclerView.setAdapter(adapter);
        binding.habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.buttonNewHabit.setOnClickListener(view1 -> {
            mHabitViewModel.select(null);
            mNavController.navigate(R.id.action_allHabitsFragment_to_newHabitFragment);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mHabitViewModel.getAllHabits().observe(this, habitList -> {
            List<Habit> allHabits = new ArrayList<>();
            for (Habit h : habitList) {
                    allHabits.add(h);
            }
            adapter.submitList(allHabits);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Habit habit) {
        mHabitViewModel.select(habit);
        mNavController.navigate(R.id.action_allHabitsFragment_to_viewHabitFragment);
    }
}