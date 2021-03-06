package com.github.cmput301f21t44.hellohabits.view.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentViewHabitBinding;
import com.github.cmput301f21t44.hellohabits.model.habit.DaysOfWeek;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;
import com.github.cmput301f21t44.hellohabits.model.habitevent.HabitEvent;
import com.github.cmput301f21t44.hellohabits.view.habitevent.HabitEventAdapter;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitEventViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Fragment class for viewing a habits
 */
public class ViewHabitFragment extends Fragment {
    private FragmentViewHabitBinding binding;
    private HabitViewModel mHabitViewModel;
    private HabitEventViewModel mHabitEventViewModel;
    private NavController mNavController;
    private HabitEventAdapter mHabitEventAdapter;
    private PreviousListViewModel mPreviousListViewModel;
    private int mPreviousListDestId;

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
        binding = FragmentViewHabitBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Initialise ViewModels
     */
    private void initViewModels() {
        // attach the provider to activity instead of fragment so the fragments can share data
        ViewModelProvider provider = ViewModelFactory.getProvider(requireActivity());
        mHabitViewModel = provider.get(HabitViewModel.class);
        mHabitEventViewModel = provider.get(HabitEventViewModel.class);
        mPreviousListViewModel = provider.get(PreviousListViewModel.class);

        //noinspection ConstantConditions
        this.mPreviousListDestId = mPreviousListViewModel.getDestinationId().getValue();
    }

    /**
     * Initialise button OnClick listeners and BackPressedCallback
     */
    private void initListeners() {
        binding.buttonEditHabit.setOnClickListener(v -> navigateOut(R.id.HabitCreateEditFragment));
        binding.buttonNewHabitEvent.setOnClickListener(v -> {
            mHabitEventViewModel.setSelectedEvent(null);
            navigateOut(R.id.EventCreateEditFragment);
        });

        binding.buttonDeleteHabit.setOnClickListener(this::createDeleteHabitDialog);
    }

    private void navigateOut(int resId) {
        mPreviousListViewModel.setFromViewHabit(false);
        mNavController.navigate(resId);
    }

    /**
     * This function setup the HabitEvent adapter
     * And set the jump-out confirmation dialog warning user when a user want to delete a habit event
     * And set OnClickListeners for the buttons in this page
     *
     * @param view               a default view
     * @param savedInstanceState a default Bundle
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModels();

        mNavController = NavHostFragment.findNavController(this);
        mHabitEventAdapter = HabitEventAdapter.newInstance(habitEvent -> {
            mHabitEventViewModel.setSelectedEvent(habitEvent);
            navigateOut(R.id.EventCreateEditFragment);
        }, habitEvent -> {
            mHabitEventViewModel.setSelectedEvent(habitEvent);
            navigateOut(R.id.EventCreateEditFragment);
        }, this::deleteHabitEvent);

        binding.habitEventList.setAdapter(mHabitEventAdapter);
        binding.habitEventList.setLayoutManager(new LinearLayoutManager(getContext()));

        initListeners();
    }

    /**
     * This function remove a selected habit from the habit list and jump back to last page
     */
    private void deleteHabit() {
        mHabitViewModel.delete(mHabitViewModel.getSelectedHabit().getValue(),
                () -> navigateOut(mPreviousListDestId),
                e -> Toast.makeText(requireActivity(),
                        "Failed to delete habit: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    /**
     * This function write the details of a habit to screen for user to view
     */
    @Override
    public void onStart() {
        super.onStart();
        mHabitViewModel.getSelectedHabit().observe(getViewLifecycleOwner(), habit -> {
            // update UI
            binding.viewTitle.setText(habit.getTitle());
            binding.viewReason.setText(habit.getReason());
            String date = DateTimeFormatter
                    .ofPattern("eeee d MMMM, y")
                    .withZone(ZoneId.systemDefault())
                    .format(habit.getDateStarted());
            binding.viewDateStarted.setText(date);
            binding.viewReminder.setText(DaysOfWeek.toString(habit.getDaysOfWeek()));

            String initialConsistency = Integer.valueOf((int) (Habit
                    .getConsistency(habit) * 100)).toString() + " %";

            binding.viewConsistency.setText(initialConsistency);
            binding.viewPrivacy.setText(habit.isPrivate() ? R.string.isPrivate : R.string.isPublic);

            mHabitEventViewModel.getHabitEventsById(habit.getId()).observe(this,
                    eventList -> {
                        final String consistency = Integer.valueOf((int) (Habit
                                .getConsistency(habit, eventList) * 100)).toString() + " %";

                        binding.viewConsistency.setText(consistency);
                        mHabitEventAdapter.submitList(eventList);
                    });
        });


        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mNavController.navigate(mPreviousListDestId);
            }
        });
    }

    /**
     * This function handle the back error event while user in this page
     */
    @Override
    public void onResume() {
        super.onResume();
        mPreviousListViewModel.getDestinationId()
                .observe(this, id -> this.mPreviousListDestId = id);
        mPreviousListViewModel.setFromViewHabit(true);
    }

    /**
     * This function close the current function and go to the last page
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * This function deletes a habit event and sends you back to the habit view
     *
     * @param habitEvent
     */
    private void deleteHabitEvent(HabitEvent habitEvent) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Habit Event")
                .setMessage("Are you sure you want to delete this habit event?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YES",
                        (dialog, b) -> mHabitEventViewModel.delete(habitEvent,
                                e -> Toast.makeText(requireActivity(),
                                        "Failed to delete event: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show()))
                .setNegativeButton("NO", null).show();
    }

    /**
     * Creates a diaglog to confirm the Delete Habit request
     *
     * @param v
     */
    private void createDeleteHabitDialog(View v) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete this habit?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YES", (dialog, b) -> deleteHabit())
                .setNegativeButton("NO", null).show();
    }
}