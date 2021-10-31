package com.github.cmput301f21t44.hellohabits.view.habitevent;

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
import com.github.cmput301f21t44.hellohabits.databinding.FragmentHabitEventListBinding;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitEventViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;

import java.util.Objects;

/**
 * A fragment representing a list of Items.
 */
public class HabitEventListFragment extends Fragment {
    private HabitEventAdapter adapter;
    private FragmentHabitEventListBinding binding;
    private NavController mNavController;
    private HabitEventViewModel mHabitEventViewModel;
    private HabitViewModel mHabitViewModel;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HabitEventListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHabitEventListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = NavHostFragment.findNavController(this);
        mHabitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        mHabitEventViewModel = new ViewModelProvider(requireActivity())
                .get(HabitEventViewModel.class);
        adapter = HabitEventAdapter.newInstance(habitEvent -> {
            mHabitEventViewModel.select(habitEvent);
            mNavController.navigate(R.id.action_habitEventListFragment_to_createEditHabitEventFragment);
        });
        binding.habitEventRecyclerView.setAdapter(adapter);
        binding.habitEventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.buttonNewEvent.setOnClickListener(v -> {
            mHabitEventViewModel.select(null);
            mNavController.navigate(R.id.action_habitEventListFragment_to_createEditHabitEventFragment);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        String habitId = Objects.requireNonNull(mHabitViewModel.getSelected().getValue()).getId();
        mHabitEventViewModel.getHabitEventsById(habitId).observe(this, habitEvents -> adapter.submitList(habitEvents));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}