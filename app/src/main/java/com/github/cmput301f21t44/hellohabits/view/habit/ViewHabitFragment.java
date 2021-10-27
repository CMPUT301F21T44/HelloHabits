package com.github.cmput301f21t44.hellohabits.view.habit;

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

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentViewHabitBinding;
import com.github.cmput301f21t44.hellohabits.model.DaysOfWeek;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitEventViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ViewHabitFragment extends Fragment {
    private FragmentViewHabitBinding binding;
    private HabitViewModel mHabitViewModel;
    private HabitEventViewModel mHabitEventViewModel;
    private NavController mNavController;

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
        mHabitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        mHabitEventViewModel = new ViewModelProvider(requireActivity())
                .get(HabitEventViewModel.class);

        mNavController = NavHostFragment.findNavController(this);

        binding.buttonBackToList.setOnClickListener(v -> mNavController
                .navigate(R.id.action_viewHabitFragment_to_todaysHabitsFragment));

        binding.buttonEditHabit.setOnClickListener(v -> mNavController
                .navigate(R.id.action_viewHabitFragment_to_createEditHabitFragment));

        binding.buttonNewHabitEvent.setOnClickListener(v -> mNavController
                .navigate(R.id.action_viewHabitFragment_to_createEditHabitEventFragment));

        binding.buttonNewHabitEvent.setOnClickListener(v -> {
            mHabitEventViewModel.select(null);
            mNavController
                    .navigate(R.id.action_viewHabitFragment_to_createEditHabitEventFragment);
        });


        binding.buttonDeleteHabit.setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Habit")
                        .setMessage("Are you sure you want to delete this habit?")
                        .setIcon(R.drawable.ic_launcher_foreground)
                        .setPositiveButton("YES", (dialog, b) -> deleteHabit())
                        .setNegativeButton("NO", null).show()
        );
    }

    private void deleteHabit() {
        mHabitViewModel.delete(mHabitViewModel.getSelected().getValue());
        mNavController.navigate(R.id.action_viewHabitFragment_to_todaysHabitsFragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        mHabitViewModel.getSelected().observe(getViewLifecycleOwner(), habit -> {
            // update UI
            binding.viewTitle.setText(habit.getTitle());
            binding.viewReason.setText(habit.getReason());
            String date = DateTimeFormatter
                    .ofPattern("eeee d MMMM, y")
                    .withZone(ZoneId.systemDefault())
                    .format(habit.getDateStarted());
            binding.viewDateStarted.setText(date);
            binding.viewReminder.setText(DaysOfWeek.toString(habit.getDaysOfWeek()));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}