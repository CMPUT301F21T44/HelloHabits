package com.github.cmput301f21t44.hellohabits.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentFirstBinding;
import com.github.cmput301f21t44.hellohabits.db.HabitEntity;
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private HabitViewModel mHabitViewModel;
    private HabitAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHabitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);
        adapter = new HabitAdapter(new HabitAdapter.HabitDiff());
        binding.habitRecyclerView.setAdapter(adapter);
        binding.habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        binding.buttonFirst.setOnClickListener(view1 ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment));
    }

    @Override
    public void onStart() {
        super.onStart();
        mHabitViewModel.getAllHabits().observe(this, list -> {
            // need to cast List<HabitEntity> to List<Habit>
            List<Habit> habits = new ArrayList<>(list);
            adapter.submitList(habits);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}