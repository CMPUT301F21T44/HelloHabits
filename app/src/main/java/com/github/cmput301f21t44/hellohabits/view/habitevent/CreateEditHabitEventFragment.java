package com.github.cmput301f21t44.hellohabits.view.habitevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentCreateEditHabitEventBinding;
import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.view.habit.CreateEditHabitFragment;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitEventViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.util.Objects;

public class CreateEditHabitEventFragment extends Fragment {
    private static final int MAX_COMMENT_LEN = 20;
    private FragmentCreateEditHabitEventBinding binding;
    private HabitViewModel mHabitViewModel;
    private HabitEventViewModel mHabitEventViewModel;
    private HabitEvent mHabitEvent;
    private boolean isEdit;
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
        binding = FragmentCreateEditHabitEventBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    private void showErrorToast(String text, Exception error) {
        Toast.makeText(requireActivity(), text + ": " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * This function does the input validation for both construction of a new habit event and editing of an exiting habit event
     * If the input habit event comment is over 20 characters it will throw the warning
     * And it submits the updated/new habit event to the view model
     */

    private void submitHabitEvent() {
        String comment = binding.editTextComment.getText().toString();
        if (comment.length() > MAX_COMMENT_LEN) {
            binding.editTextComment.setError(CreateEditHabitFragment.TOO_LONG_ERROR_MESSAGE);
            binding.editTextComment.requestFocus();
            return;
        }
        if (isEdit) {
            mHabitEventViewModel.update(mHabitEvent.getId(),
                    mHabitEvent.getHabitId(), mHabitEvent.getDate(), comment,
                    (updatedHabitEvent) -> {
                        mHabitEventViewModel.setSelectedEvent(updatedHabitEvent);
                        mNavController.navigate(R.id.viewHabitFragment);
                    },
                    (e) -> showErrorToast("Failed to update habit", e));
        } else {
            String habitId = Objects.requireNonNull(mHabitViewModel.getSelectedHabit().getValue())
                    .getId();
            mHabitEventViewModel.insert(habitId, comment, () -> {
                mNavController.navigate(R.id.viewHabitFragment);
            }, e -> showErrorToast("Failed to add habit", e));
        }
    }

    /**
     * This function set up the ViewModel of Habit and habit event
     * And it sets the OnClickListener for buttons in this page
     *
     * @param view               a default view
     * @param savedInstanceState a default Bundle
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModelProvider provider = ViewModelFactory.getProvider(requireActivity());
        mHabitViewModel = provider.get(HabitViewModel.class);
        mHabitEventViewModel = provider.get(HabitEventViewModel.class);
        mNavController = NavHostFragment.findNavController(this);

        binding.buttonAddHabitEvent.setOnClickListener(v -> submitHabitEvent());
        binding.buttonAddLocation.setOnClickListener(v -> getLocation());

        binding.buttonAddPhoto.setOnClickListener(v -> getPhoto());
    }

    private void getLocation() {
        Toast.makeText(getActivity(), "Not implemented yet!", Toast.LENGTH_SHORT).show();

    }


    private void getPhoto() {
        Toast.makeText(getActivity(), "Not implemented yet!", Toast.LENGTH_SHORT).show();
    }

    /**
     * This function sets the boolean isEdit value: true for edited habit event,false for new habit event
     */
    @Override
    public void onStart() {
        super.onStart();
        mHabitEventViewModel.getSelectedEvent().observe(getViewLifecycleOwner(), habitEvent -> {
            if (habitEvent == null) {
                isEdit = false;
            } else {
                isEdit = true;
                // update UI
                mHabitEvent = habitEvent;
                binding.editTextComment.setText(habitEvent.getComment());
            }
            String habitTitle =
                    Objects.requireNonNull(mHabitViewModel.getSelectedHabit().getValue()).getTitle();
            binding.habitTitle.setText(habitTitle);
        });
    }

    /**
     * This function close the current function and go to the last page
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}