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
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitEventViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;

import java.util.Objects;

public class CreateEditHabitEventFragment extends Fragment {
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

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHabitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        mHabitEventViewModel = new ViewModelProvider(requireActivity())
                .get(HabitEventViewModel.class);
        mNavController = NavHostFragment.findNavController(this);

        binding.buttonAddHabitEvent.setOnClickListener(v -> {
            if (isEdit) {
                HabitEvent updatedHabitEvent = mHabitEventViewModel.update(mHabitEvent.getId(),
                        mHabitEvent.getDate(), binding.editTextComment.getText().toString(), null,
                        null);
                mHabitEventViewModel.select(updatedHabitEvent);
            } else {
                mHabitEventViewModel.insert(
                        Objects.requireNonNull(mHabitViewModel.getSelected().getValue()).getId(),
                        binding.editTextComment.getText().toString());
            }
            mNavController.navigate(R.id.action_createEditHabitEventFragment_to_viewHabitFragment);
        });

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