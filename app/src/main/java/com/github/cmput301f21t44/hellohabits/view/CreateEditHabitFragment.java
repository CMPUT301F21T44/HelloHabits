package com.github.cmput301f21t44.hellohabits.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentCreateEditHabitBinding;
import com.github.cmput301f21t44.hellohabits.model.DaysOfWeek;
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CreateEditHabitFragment extends Fragment {
    private FragmentCreateEditHabitBinding binding;
    private HabitViewModel mHabitViewModel;
    private Habit mHabit;
    private Instant mInstant;
    private boolean isEdit;
    private boolean[] mDaysOfWeek;
    private NavController mNavController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateEditHabitBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void updateInstant(Instant instant) {
        mInstant = instant;
        String date = DateTimeFormatter
                .ofPattern("eeee d MMMM, y").withZone(ZoneId.systemDefault()).format(mInstant);
        binding.textDateStarted.setText(date);
    }

    private void updateDaysOfWeek(boolean[] daysOfWeek) {
        mDaysOfWeek = daysOfWeek;
        binding.daysOfWeek.setText(DaysOfWeek.toString(daysOfWeek));
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHabitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        mNavController = NavHostFragment.findNavController(this);

        binding.buttonAddHabit.setOnClickListener(v -> {
            if (isEdit) {
                Habit updatedHabit = mHabitViewModel.update(mHabit.getId(),
                        binding.editTextTitle.getText().toString(),
                        binding.editTextReason.getText().toString(), mInstant, mDaysOfWeek);
                mHabitViewModel.select(updatedHabit);
            } else {
                mHabitViewModel.insert(binding.editTextTitle.getText().toString(),
                        binding.editTextReason.getText().toString(), mInstant, mDaysOfWeek);
            }
            mNavController.navigate(isEdit
                    ? R.id.action_createEditHabitFragment_to_viewHabitFragment
                    : R.id.action_createEditHabitFragment_to_todaysHabitsFragment);
        });

        binding.textDateStarted.setOnClickListener(v -> startDatePickerFragment());
        binding.dateStartedLabel.setOnClickListener(v -> startDatePickerFragment());

        binding.buttonBack.setOnClickListener(v ->
                mNavController.navigate(isEdit
                        ? R.id.action_createEditHabitFragment_to_viewHabitFragment
                        : R.id.action_createEditHabitFragment_to_todaysHabitsFragment)
        );

        binding.reminderLabel.setOnClickListener(v -> startDaysOfWeekFragment());
        binding.daysOfWeek.setOnClickListener(v -> startDaysOfWeekFragment());
    }

    private void startDatePickerFragment() {
        DialogFragment newFragment = DatePickerFragment.newInstance(
                (datePicker, year, month, day) ->
                        updateInstant(LocalDate.of(year, month + 1, day)
                                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
    }

    private void startDaysOfWeekFragment() {
        DialogFragment newFragment = DaysOfWeekFragment
                .newInstance(mDaysOfWeek, this::updateDaysOfWeek);
        newFragment.show(getParentFragmentManager(), "daysOfWeek");
    }

    @Override
    public void onStart() {
        super.onStart();
        mHabitViewModel.getSelected().observe(getViewLifecycleOwner(), habit -> {
            isEdit = habit != null;
            if (isEdit) {
                // populate fields with habit data
                mHabit = habit;
                binding.editTextTitle.setText(habit.getTitle());
                binding.editTextReason.setText(habit.getReason());
                updateInstant(habit.getDateStarted());
                updateDaysOfWeek(habit.getDaysOfWeek());

                binding.buttonAddHabit.setText(R.string.save_changes);
            } else {
                updateInstant(Instant.now());
                updateDaysOfWeek(DaysOfWeek.emptyArray());
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}