package com.github.cmput301f21t44.hellohabits.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentSecondBinding;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private HabitViewModel mHabitViewModel;
    private Instant mInstant;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
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
        updateInstant(Instant.now());

        binding.buttonAddHabit.setOnClickListener(v -> {
            mHabitViewModel.insert(
                    binding.editTextTitle.getText().toString(),
                    binding.editTextReason.getText().toString(),
                    mInstant);
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        });

        binding.textDateStarted.setOnClickListener(v -> {
            DialogFragment newFragment = DatePickerFragment.newInstance(
                    (datePicker, year, month, day) -> updateInstant(
                            LocalDate.of(year, month + 1, day)
                                    .atStartOfDay(ZoneId.systemDefault()).toInstant()
                    )
            );

            newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
        });

        binding.buttonSecond.setOnClickListener(view1 -> NavHostFragment
                .findNavController(SecondFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}