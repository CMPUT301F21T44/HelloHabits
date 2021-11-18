package com.github.cmput301f21t44.hellohabits.view.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentAllHabitsBinding;
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
            mPreviousListViewModel.setDestinationId(R.id.allHabitsFragment);
            mNavController.navigate(R.id.action_allHabitsFragment_to_viewHabitFragment);
        });
        mBinding.habitRecyclerView.setAdapter(mAdapter);
        mBinding.habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mBinding.habitRecyclerView);

        mBinding.buttonNewHabit.setOnClickListener(view1 -> {
            mHabitViewModel.setSelectedHabit(null);
            mPreviousListViewModel.setDestinationId(R.id.allHabitsFragment);
            mNavController.navigate(R.id.action_allHabitsFragment_to_newHabitFragment);
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
     * This function handle the back button event when the fragment is on resume
     */
    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        mNavController.navigate(R.id.TodaysHabitsFragment);
                    }
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

    /**
     * Allows the user to rearrange the list as they please
     */
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
            ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            List<Habit> allHabits = new ArrayList<>(mAdapter.getCurrentList());
            Collections.swap(allHabits, fromPosition, toPosition);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
}