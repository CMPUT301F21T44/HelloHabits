package com.github.cmput301f21t44.hellohabits.view.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentAllHabitsBinding;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for viewing all habits
 */
public class AllHabitsFragment extends Fragment {
    private FragmentAllHabitsBinding mBinding;
    private HabitViewModel mHabitViewModel;
    private PreviousListViewModel mPreviousListViewModel;
    private HabitAdapter mAdapter;
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
        mBinding = FragmentAllHabitsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    /**
     * @param view               a default view
     * @param savedInstanceState a default Bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = NavHostFragment.findNavController(this);
        // attach the provider to activity instead of fragment so the fragments can share data
        ViewModelProvider provider = ViewModelFactory.getProvider(requireActivity());
        mHabitViewModel = provider.get(HabitViewModel.class);
        mPreviousListViewModel = provider.get(PreviousListViewModel.class);
        mAdapter = HabitAdapter.newInstance((habit) -> {
            mHabitViewModel.setSelectedHabit(habit);
            mPreviousListViewModel.setDestinationId(R.id.AllHabitsFragment);
            mNavController.navigate(R.id.ViewHabitFragment);
        });
        mBinding.habitRecyclerView.setAdapter(mAdapter);
        mBinding.habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mBinding.buttonNewHabit.setOnClickListener(view1 -> {
            mHabitViewModel.setSelectedHabit(null);
            mPreviousListViewModel.setDestinationId(R.id.AllHabitsFragment);
            mNavController.navigate(R.id.HabitCreateEditFragment);
        });
    }

    /**
     * This function get and list all the habits on screen when go to this fragment
     */
    @Override
    public void onStart() {
        super.onStart();
        mHabitViewModel.getAllHabits().observe(this, habitList -> {
            List<Habit> allHabits = new ArrayList<>(habitList);
            mAdapter.submitList(allHabits);
        });
    }

    /**
     * This function close the current page and return to the last page
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}