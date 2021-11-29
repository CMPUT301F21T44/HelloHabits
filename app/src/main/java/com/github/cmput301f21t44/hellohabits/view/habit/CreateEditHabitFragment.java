package com.github.cmput301f21t44.hellohabits.view.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentCreateEditHabitBinding;
import com.github.cmput301f21t44.hellohabits.model.habit.DaysOfWeek;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PhotoViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Fragment for creating or editing a Habit
 */
public class CreateEditHabitFragment extends Fragment {
    public static final String TOO_LONG_ERROR_MESSAGE = "Input is too long";
    public static final String EMPTY_ERROR_MESSAGE = "Input cannot be empty";
    public static final int MAX_TITLE_LEN = 20;
    public static final int MAX_REASON_LEN = 30;

    private FragmentCreateEditHabitBinding binding;
    private HabitViewModel mHabitViewModel;
    private Habit mHabit;
    private Instant mInstant;
    private boolean mIsEdit;
    private boolean[] mDaysOfWeek;
    private boolean mIsPrivate;
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
     * Set the Habit visibility and update the UI accordingly
     *
     * @param isPrivate whether the Habit should be visible to others or not
     */
    private void updatePrivate(boolean isPrivate) {
        this.mIsPrivate = isPrivate;
        binding.privateCheckbox.setChecked(isPrivate);
    }

    /**
     * Set the current start date and update the UI accordingly
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
     * Set the current days of week and update the UI accordingly
     *
     * @param daysOfWeek a boolean array standing for the days for habit events
     */
    private void updateDaysOfWeek(boolean[] daysOfWeek) {
        mDaysOfWeek = daysOfWeek;
        binding.daysOfWeek.setText(DaysOfWeek.toString(daysOfWeek));
    }

    /**
     * Validates and extract input from an EditText view
     *
     * @param field     EditText view to extract input from
     * @param maxLength Maximum character length for the input
     * @return The input String if valid, null if invalid
     */
    private String validateInput(EditText field, int maxLength) {
        String input = field.getText().toString();
        if (input.isEmpty() || input.length() > maxLength) {
            field.requestFocus();
            field.setError(input.isEmpty() ? EMPTY_ERROR_MESSAGE : TOO_LONG_ERROR_MESSAGE);
            return null;
        }
        return input;
    }

    /**
     * Handles the creation of a new Habit or modification of an existing Habit
     */
    private void submitHabit() {
        String title = validateInput(binding.editTextTitle, MAX_TITLE_LEN);
        String reason = validateInput(binding.editTextReason, MAX_REASON_LEN);

        if (title == null || reason == null) return;

        if (mIsEdit) {
            mHabitViewModel.update(mHabit.getId(), title, reason, mInstant,
                    mDaysOfWeek, mIsPrivate, mHabit.getIndex(), updatedHabit -> {
                        mHabitViewModel.setSelectedHabit(updatedHabit);
                        completeScreen();
                    }, e -> showErrorToast("update", e));
        } else {
            mHabitViewModel.insert(title, reason, mInstant, mDaysOfWeek, mIsPrivate,
                    this::completeScreen, e -> showErrorToast("create", e));
        }
    }

    /**
     * Show Toast for an operation error
     *
     * @param operation Operation that failed
     * @param error     Exception passed from failing callback
     */
    private void showErrorToast(String operation, Exception error) {
        Toast.makeText(requireActivity(),
                "Failed to " + operation + " habit: " + error.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Complete create/edit Habit
     * <p>
     * If editing a Habit, navigate to View Habit screen
     * <p>
     * if creating a Habit, navigate to the previous list visited (Today's Habits or All Habits)
     */
    private void completeScreen() {
        // If created a new habit, this would either be TodaysHabitsFragment or AllHabitsFragment
        int previousDest = Objects.requireNonNull(mNavController.getPreviousBackStackEntry())
                .getDestination().getId();

        mNavController.navigate(mIsEdit ? R.id.ViewHabitFragment : previousDest);

    }

    /**
     * CreateEditHabitEventFragment's Lifecycle onViewCreated method
     * <p>
     * Initializes member variables and button OnClickListeners
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

        binding.privateCheckbox.setOnClickListener(v -> updatePrivate(!mIsPrivate));
    }

    /**
     * Starts a dialog fragment to select the start date
     */
    private void startDatePickerFragment() {
        DialogFragment newFragment = DatePickerFragment.newInstance(
                (datePicker, year, month, day) ->
                        updateInstant(LocalDate.of(year, month + 1, day)
                                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
    }

    /**
     * Starts a dialog fragment to select days of a week for when this Habit is scheduled to be done
     */
    private void startDaysOfWeekFragment() {
        DialogFragment newFragment = DaysOfWeekFragment
                .newInstance(mDaysOfWeek, this::updateDaysOfWeek);
        newFragment.show(getParentFragmentManager(), "daysOfWeek");
    }

    /**
     * CreateEditHabitEventFragment's Lifecycle onStart method
     */
    @Override
    public void onStart() {
        super.onStart();
        mHabitViewModel.getSelectedHabit().observe(getViewLifecycleOwner(), this::onHabitChanged);
    }

    /**
     * This function close the current page and go back to last page
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Updates the UI to the currently observed Habit
     *
     * @param habit The current Habit to be shown on screen, null for new Habit
     */
    private void onHabitChanged(Habit habit) {
        mIsEdit = habit != null;
        if (mIsEdit) {
            // populate fields with habit data
            mHabit = habit;
            binding.editTextTitle.setText(habit.getTitle());
            binding.editTextReason.setText(habit.getReason());
            updatePrivate(habit.isPrivate());
            updateInstant(habit.getDateStarted());
            updateDaysOfWeek(habit.getDaysOfWeek());

            binding.buttonAddHabit.setText(R.string.save_changes);
        } else {
            // use default values for new habit
            updatePrivate(false);
            updateInstant(Instant.now());
            updateDaysOfWeek(DaysOfWeek.emptyArray());
        }
    }
}