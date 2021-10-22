package com.github.cmput301f21t44.hellohabits.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentViewHabitBinding;
import com.github.cmput301f21t44.hellohabits.db.HabitEntity;
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.SelectedHabitViewModel;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ViewHabitFragment extends Fragment {
    private FragmentViewHabitBinding binding;
    private SelectedHabitViewModel mViewModel;
    private HabitViewModel mHabitViewModel;
    private Instant mInstant;
    private Habit mHabit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewHabitBinding.inflate(inflater, container, false);
        return binding.getRoot();


    }

    private void updateInstant(Instant instant) {
        mInstant = instant;
        String date = DateTimeFormatter
                .ofPattern("eeee d MMMM, y").withZone(ZoneId.systemDefault()).format(mInstant);
        binding.viewDateStarted.setText(date);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // attach the provider to activity instead of fragment so the fragments can share data
        mViewModel = new ViewModelProvider(requireActivity())
                .get(SelectedHabitViewModel.class);

        mHabitViewModel = new ViewModelProvider(requireActivity())
                .get(HabitViewModel.class);

        updateInstant(Instant.now());

        binding.buttonBackToList.setOnClickListener(v ->
                NavHostFragment
                        .findNavController(ViewHabitFragment.this)
                        .navigate(R.id.action_viewHabitFragment_to_todaysHabitsFragment));

        binding.viewDateStarted.setOnClickListener(v -> {
            DialogFragment newFragment = DatePickerFragment.newInstance(
                    (datePicker, year, month, day) ->
                            updateInstant(
                                    LocalDate
                                            .of(year, month + 1, day)
                                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));

            newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
        });

        binding.buttonSaveChanges.setOnClickListener(v -> {
            mHabit.setTitle(binding.viewTitle.getText().toString());
            mHabit.setReason(binding.viewReason.getText().toString());
            mHabit.setDateStarted(mInstant);
            mHabitViewModel.update(mHabit);
        });

        binding.buttonDeleteHabit.setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Habit")
                        .setMessage("Are you sure you want to delete this habit?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> deleteHabit())
                        .setNegativeButton(android.R.string.no, null).show()
        );
    }

    private void deleteHabit() {
        mHabitViewModel.delete(mViewModel.getSelected().getValue());
        NavHostFragment
                .findNavController(ViewHabitFragment.this)
                .navigate(R.id.action_viewHabitFragment_to_todaysHabitsFragment);

    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel.getSelected().observe(getViewLifecycleOwner(), habit -> {
            mHabit = habit;
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