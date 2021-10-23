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
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.SelectedHabitViewModel;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CreateEditHabitFragment extends Fragment {
    private FragmentCreateEditHabitBinding binding;
    private HabitViewModel mHabitViewModel;
    private SelectedHabitViewModel mSelectedViewModel;
    private Habit mHabit;
    private Instant mInstant;
    private boolean isEdit;
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

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHabitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);
        mSelectedViewModel = new ViewModelProvider(requireActivity()).get(SelectedHabitViewModel.class);
        mNavController = NavHostFragment.findNavController(this);

        binding.buttonAddHabit.setOnClickListener(v -> {
            if (isEdit) {
                Habit updatedHabit = mHabitViewModel.update(
                        mHabit.getId(),
                        binding.editTextTitle.getText().toString(),
                        binding.editTextReason.getText().toString(),
                        mInstant);
                mSelectedViewModel.select(updatedHabit);
            } else {
                mHabitViewModel.insert(
                        binding.editTextTitle.getText().toString(),
                        binding.editTextReason.getText().toString(),
                        mInstant);
            }
            mNavController.navigate(isEdit
                    ? R.id.action_createEditHabitFragment_to_viewHabitFragment
                    : R.id.action_createEditHabitFragment_to_todaysHabitsFragment);
        });

        binding.textDateStarted.setOnClickListener(v -> {
            DialogFragment newFragment = DatePickerFragment.newInstance(
                    (datePicker, year, month, day) ->
                            updateInstant(
                                    LocalDate
                                            .of(year, month + 1, day)
                                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));

            newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
        });

        binding.buttonBack.setOnClickListener(v ->
                mNavController.navigate(isEdit
                        ? R.id.action_createEditHabitFragment_to_viewHabitFragment
                        : R.id.action_createEditHabitFragment_to_todaysHabitsFragment)
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        mSelectedViewModel.getSelected().observe(getViewLifecycleOwner(), habit -> {
            if (habit == null) {
                isEdit = false;
                updateInstant(Instant.now());
            } else {
                isEdit = true;
                binding.buttonAddHabit.setText("Save Changes");
                // update UI
                mHabit = habit;
                binding.editTextTitle.setText(habit.getTitle());
                binding.editTextReason.setText(habit.getReason());
                updateInstant(habit.getDateStarted());
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}