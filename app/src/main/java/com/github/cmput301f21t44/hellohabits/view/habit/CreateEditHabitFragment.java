package com.github.cmput301f21t44.hellohabits.view.habit;

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
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class CreateEditHabitFragment extends Fragment {
    public static final String ERROR_MESSAGE = "Input is too long";
    private static final int MAX_TITLE_LEN = 20;
    private static final int MAX_REASON_LEN = 30;

    private FragmentCreateEditHabitBinding binding;
    private HabitViewModel mHabitViewModel;
    private Habit mHabit;
    private Instant mInstant;
    private boolean isEdit;
    private boolean[] mDaysOfWeek;
    private NavController mNavController;

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
        binding = FragmentCreateEditHabitBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * This function update/set the start date of a exiting/new habit
     *
     * @param instant an Instant - the start date before updated
     */
    private void updateInstant(Instant instant) {
        mInstant = instant;
        String date = DateTimeFormatter
                .ofPattern("MMMM d, y").withZone(ZoneId.systemDefault()).format(mInstant);
        binding.textDateStarted.setText(date);
    }

    /**
     * This function update/set the days for habit events in every week for an exiting/new habit
     *
     * @param daysOfWeek a boolean array standing for the days for habit events
     */
    private void updateDaysOfWeek(boolean[] daysOfWeek) {
        mDaysOfWeek = daysOfWeek;
        binding.daysOfWeek.setText(DaysOfWeek.toString(daysOfWeek));
    }

    /**
     * This function does the input validation for both construction of a new habit and editing of an exiting habit
     * If the input habit title is over 20 characters it will throw the warning
     * If the input habit reason is over 30 characters it will throw the warning
     * And it submits the updated/new habit to the view model
     */
    private void submitHabit() {
        boolean validTitle = true, validReason = true;

        String title = binding.editTextTitle.getText().toString();
        if (title.length() > MAX_TITLE_LEN) {
            binding.editTextTitle.setError(ERROR_MESSAGE);
            binding.editTextTitle.requestFocus();
            validTitle = false;
        }

        String reason = binding.editTextReason.getText().toString();
        if (reason.length() > MAX_REASON_LEN) {
            binding.editTextReason.setError(ERROR_MESSAGE);
            binding.editTextReason.requestFocus();
            validReason = false;
        }

        if (!validTitle || !validReason) return;

        if (isEdit) {
            Habit updatedHabit = mHabitViewModel.update(mHabit.getId(), title, reason, mInstant,
                    mDaysOfWeek);
            mHabitViewModel.select(updatedHabit);
        } else {
            mHabitViewModel.insert(title, reason, mInstant, mDaysOfWeek);
        }

        // If created a new habit, this would either be TodaysHabitsFragment or AllHabitsFragment
        int previousDest = Objects.requireNonNull(mNavController.getPreviousBackStackEntry())
                .getDestination().getId();

        mNavController.navigate(isEdit ? R.id.viewHabitFragment : previousDest);
    }

    /**
     * This function set the OnClickerListener to buttons in this page
     *
     * @param view               a default view
     * @param savedInstanceState a default Bundle
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModelProvider provider = ViewModelFactory.getProvider(requireActivity());
        mHabitViewModel = provider.get(HabitViewModel.class);
        mNavController = NavHostFragment.findNavController(this);

        binding.buttonAddHabit.setOnClickListener(v -> submitHabit());

        binding.dateStartedLayout.setOnClickListener(v -> startDatePickerFragment());

        binding.reminderLayout.setOnClickListener(v -> startDaysOfWeekFragment());
    }

    /**
     * This function provides a fragment to select the start date
     */
    private void startDatePickerFragment() {
        DialogFragment newFragment = DatePickerFragment.newInstance(
                (datePicker, year, month, day) ->
                        updateInstant(LocalDate.of(year, month + 1, day)
                                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
    }

    /**
     * This function provides a fragment to select days of a week for habit event
     */
    private void startDaysOfWeekFragment() {
        DialogFragment newFragment = DaysOfWeekFragment
                .newInstance(mDaysOfWeek, this::updateDaysOfWeek);
        newFragment.show(getParentFragmentManager(), "daysOfWeek");
    }

    /**
     * This function do a judgement to see if this page is called to edit a habit or create a habit at the beginning
     */
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

    /**
     * This function close the current page and go back to last page
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}