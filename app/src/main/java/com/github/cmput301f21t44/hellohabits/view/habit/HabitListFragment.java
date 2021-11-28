package com.github.cmput301f21t44.hellohabits.view.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentHabitListBinding;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;
import com.github.cmput301f21t44.hellohabits.view.OnItemClickListener;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Fragment for viewing a list of habits
 */
public abstract class HabitListFragment extends Fragment {
    /**
     * Touch Helper for dragging Habit items to reorder
     */
    private final ItemTouchHelper mItemTouchHelper = new HabitTouchHelper();
    /**
     * List to keep track of index changes to Habits
     */
    private final List<HabitIndexChange> mIndexChangeList = new ArrayList<>();

    protected HabitViewModel mHabitViewModel;
    protected PreviousListViewModel mPreviousListViewModel;
    protected HabitAdapter mAdapter;
    protected FragmentHabitListBinding mBinding;
    protected NavController mNavController;
    private boolean mReordering = false;

    /**
     * ItemAnimator to be preserved when being disabled
     */
    private RecyclerView.ItemAnimator mAnimator;

    /**
     * HabitList's onCreateView lifecycle method
     * <p>
     * Binds the ViewBinding class to the fragment
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
        mBinding = FragmentHabitListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    /**
     * HabitList's onViewCreated lifecycle method
     * <p>
     * Removes the floating action buttons by default until initListeners is called
     *
     * @param view               a default view
     * @param savedInstanceState a default Bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.newHabit.setVisibility(View.GONE);
        mBinding.reorderFab.setVisibility(View.GONE);
        mBinding.dragHint.setVisibility(View.GONE);
        mNavController = NavHostFragment.findNavController(this);
    }

    /**
     * HabitList's onStart lifecycle method
     * <p>
     * Initializes the ViewModels
     */
    @Override
    public void onStart() {
        super.onStart();
        // attach the provider to activity instead of fragment so the fragments can share data
        ViewModelProvider provider = ViewModelFactory.getProvider(requireActivity());
        mHabitViewModel = provider.get(HabitViewModel.class);
        mPreviousListViewModel = provider.get(PreviousListViewModel.class);
    }

    /**
     * Initializes the RecyclerView's HabitAdapter
     *
     * @param habitListGetter Callback for getting the habit list
     * @param listener        onClickListener for Habit List items
     */
    protected void initAdapter(HabitAdapter.GetHabitList habitListGetter,
                               OnItemClickListener<Habit> listener) {
        mAdapter = HabitAdapter.newInstance(listener, mItemTouchHelper::startDrag, mHabitViewModel,
                this, habitListGetter);
        mBinding.habitRecyclerView.setAdapter(mAdapter);
        mBinding.habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAnimator = mBinding.habitRecyclerView.getItemAnimator();
        habitListGetter.getHabits().observe(this, habits -> {
            Collections.sort(habits, Comparator.comparingInt(Habit::getIndex));
            mAdapter.submitList(habits);
        });
    }

    /**
     * Initializes the listeners for the HabitAdapter
     * <p>
     * Makes the floating action buttons visible
     *
     * @param resId destination ID for the PreviousListViewModel
     */
    protected void initListeners(int resId, HabitAdapter.GetHabitList habitListGetter) {
        initAdapter(habitListGetter, (habit) -> {
            if (mReordering) return;

            mHabitViewModel.setSelectedHabit(habit);
            mPreviousListViewModel.setDestinationId(resId);
            mNavController.navigate(R.id.ViewHabitFragment);
        });
        mBinding.newHabit.setVisibility(View.VISIBLE);
        mBinding.newHabit.setOnClickListener(v -> {
            mHabitViewModel.setSelectedHabit(null);
            mPreviousListViewModel.setDestinationId(resId);
            mNavController.navigate(R.id.HabitCreateEditFragment);
        });

        mBinding.reorderFab.setVisibility(View.VISIBLE);
        mBinding.dragHint.setVisibility(View.VISIBLE);
        mBinding.reorderFab.setOnClickListener(v -> updateReorderUI(toggleReorder()));
    }

    /**
     * Toggles the Reorder state for the Habit list
     *
     * @return true if reordering, false if not
     */
    private boolean toggleReorder() {
        final ActionBar actionBar = Objects.requireNonNull(((AppCompatActivity) requireActivity())
                .getSupportActionBar());
        this.mReordering = !mReordering;
        actionBar.setDisplayHomeAsUpEnabled(!mReordering);
        mHabitViewModel.setReordering(mReordering);
        return mReordering;
    }

    /**
     * Updates the UI components, animations and event listeners depending on the reordering state
     *
     * @param reordering the current reordering state
     */
    private void updateReorderUI(boolean reordering) {
        if (reordering) {
            // enable RecyclerView animation when reordering
            mBinding.habitRecyclerView.setItemAnimator(mAnimator);

            mItemTouchHelper.attachToRecyclerView(mBinding.habitRecyclerView);
            mBinding.reorderFab.setImageResource(R.drawable.ic_baseline_check_circle_24);
            mBinding.newHabit.setVisibility(View.INVISIBLE);
        } else {
            // check if order was changed here, update indices in FireStore
            if (orderChanged()) updateIndices();

            // need to disable animations to prevent weird swapping when changing indices
            mBinding.habitRecyclerView.setItemAnimator(null);

            mItemTouchHelper.attachToRecyclerView(null);
            mBinding.reorderFab.setImageResource(R.drawable.ic_baseline_reorder_24);
            mBinding.newHabit.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Updates the indices in FireStore
     */
    private void updateIndices() {
        mHabitViewModel.updateIndices(mIndexChangeList, (e) -> Toast.makeText(getContext(),
                "Failed to update in FireStore, changes will not be persisted.",
                Toast.LENGTH_SHORT).show());
    }

    /**
     * Checks if the list order has changed
     *
     * @return true if the order was changed, false if not
     */
    private boolean orderChanged() {
        RecyclerView.LayoutManager layoutManager = mBinding.habitRecyclerView.getLayoutManager();
        if (layoutManager == null) return false;

        int n = layoutManager.getChildCount();
        mIndexChangeList.clear();
        for (int i = 0; i < n; ++i) {
            View habitView = Objects.requireNonNull(layoutManager.getChildAt(i));
            mIndexChangeList.add(HabitIndexChange.fromView(habitView, i));
        }

        return mIndexChangeList.stream().anyMatch(c -> c.getOldIndex() != c.getNewIndex());
    }

    /**
     * HabitListFragment's onDestroyView lifecycle method
     * <p>
     * Unbinds the ViewBinding class from the Fragment
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
