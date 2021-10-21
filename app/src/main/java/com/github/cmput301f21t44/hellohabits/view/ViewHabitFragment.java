package com.github.cmput301f21t44.hellohabits.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentViewHabitBinding;
import com.github.cmput301f21t44.hellohabits.db.HabitEntity;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.SelectedHabitViewModel;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ViewHabitFragment extends Fragment {
    private FragmentViewHabitBinding binding;
    private SelectedHabitViewModel mViewModel;
    private HabitViewModel mHabitViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewHabitBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // attach the provider to activity instead of fragment so the fragments can share data
        mViewModel = new ViewModelProvider(requireActivity())
                .get(SelectedHabitViewModel.class);

        mHabitViewModel = new ViewModelProvider(requireActivity())
                .get(HabitViewModel.class);

        binding.buttonBackToList.setOnClickListener(v ->
                NavHostFragment
                        .findNavController(ViewHabitFragment.this)
                        .navigate(R.id.action_viewHabitFragment_to_todaysHabitsFragment));

        binding.buttonDeleteHabit.setOnClickListener(v -> {
            mViewModel.getSelected().observe(getViewLifecycleOwner(), habit -> {
                mHabitViewModel.delete(habit);
                NavHostFragment
                        .findNavController(ViewHabitFragment.this)
                        .navigate(R.id.action_viewHabitFragment_to_todaysHabitsFragment);

            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel.getSelected().observe(getViewLifecycleOwner(), habit -> {
            // update UI
            binding.viewTitle.setText(habit.getTitle());
            binding.viewReason.setText(habit.getReason());
            String date = DateTimeFormatter
                    .ofPattern("eeee d MMMM, y")
                    .withZone(ZoneId.systemDefault())
                    .format(habit.getDateStarted());
            binding.viewDateStarted.setText(date);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}