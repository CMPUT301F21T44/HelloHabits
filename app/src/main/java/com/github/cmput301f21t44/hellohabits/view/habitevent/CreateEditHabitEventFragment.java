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
                        mHabitEventViewModel.select(updatedHabitEvent);
                        mNavController.navigate(R.id.viewHabitFragment);
                    },
                    (e) -> showErrorToast("Failed to update habit", e));
        } else {
            String habitId = Objects.requireNonNull(mHabitViewModel.getSelected().getValue())
                    .getId();
            mHabitEventViewModel.insert(habitId, comment, () -> {
                mNavController.navigate(R.id.viewHabitFragment);
            }, e -> showErrorToast("Failed to add habit", e));
        }
    }

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

    @Override
    public void onStart() {
        super.onStart();
        mHabitEventViewModel.getSelected().observe(getViewLifecycleOwner(), habitEvent -> {
            if (habitEvent == null) {
                isEdit = false;
            } else {
                isEdit = true;
                // update UI
                mHabitEvent = habitEvent;
                binding.editTextComment.setText(habitEvent.getComment());
            }
            String habitTitle =
                    Objects.requireNonNull(mHabitViewModel.getSelected().getValue()).getTitle();
            binding.habitTitle.setText(habitTitle);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}