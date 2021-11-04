package com.github.cmput301f21t44.hellohabits.view.habitevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private void submitHabitEvent() {
        String comment = binding.editTextComment.getText().toString();
        if (comment.length() > MAX_COMMENT_LEN) {
            binding.editTextComment.setError(CreateEditHabitFragment.ERROR_MESSAGE);
            binding.editTextComment.requestFocus();
            return;
        }
        if (isEdit) {
            HabitEvent updatedHabitEvent = mHabitEventViewModel.update(mHabitEvent.getId(),
                    mHabitEvent.getHabitId(), mHabitEvent.getDate(), comment, null, null);
            mHabitEventViewModel.select(updatedHabitEvent);
        } else {
            String habitId = Objects.requireNonNull(mHabitViewModel.getSelected().getValue())
                    .getId();
            mHabitEventViewModel.insert(habitId, comment);
        }
        mNavController.navigate(R.id.viewHabitFragment);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModelProvider provider = ViewModelFactory.getProvider(requireActivity());
        mHabitViewModel = provider.get(HabitViewModel.class);
        mHabitEventViewModel = provider.get(HabitEventViewModel.class);
        mNavController = NavHostFragment.findNavController(this);

        binding.buttonAddHabitEvent.setOnClickListener(v -> submitHabitEvent());

        binding.buttonBack.setOnClickListener(v ->
                mNavController
                        .navigate(R.id.action_createEditHabitEventFragment_to_viewHabitFragment));
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
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}