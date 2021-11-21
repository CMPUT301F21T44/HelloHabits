package com.github.cmput301f21t44.hellohabits.view.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.FragmentHabitListBinding;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;
import com.github.cmput301f21t44.hellohabits.view.OnItemClickListener;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.PreviousListViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

/**
 * Fragment for viewing a list of habits
 */
public abstract class HabitListFragment extends Fragment {
    protected HabitViewModel mHabitViewModel;
    protected HabitAdapter mAdapter;
    protected FragmentHabitListBinding mBinding;

    protected PreviousListViewModel mPreviousListViewModel;
    protected NavController mNavController;
    protected boolean mReordering = false;

    private FloatingActionButton mNewHabitFab;
    private FloatingActionButton mReorderFab;

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
        this.mNewHabitFab = mBinding.newHabit;
        mNewHabitFab.setVisibility(View.GONE);
        this.mReorderFab = mBinding.reorderFab;
        mReorderFab.setVisibility(View.GONE);
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
     * @param listener onClickListener for Habit List items
     */
    protected void initAdapter(OnItemClickListener<Habit> listener) {
        mAdapter = HabitAdapter.newInstance(listener);
        mBinding.habitRecyclerView.setAdapter(mAdapter);
        mBinding.habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Initializes the listeners for the HabitAdapter
     * <p>
     * Also makes the floating action buttons visible
     *
     * @param resId destination ID for the PreviousListViewModel
     */
    protected void initListeners(int resId) {
        initAdapter((habit) -> {
            if (mReordering) return;

            mHabitViewModel.setSelectedHabit(habit);
            mPreviousListViewModel.setDestinationId(resId);
            mNavController.navigate(R.id.ViewHabitFragment);
        });

        mNewHabitFab.setVisibility(View.VISIBLE);
        mNewHabitFab.setOnClickListener(v -> {
            mHabitViewModel.setSelectedHabit(null);
            mPreviousListViewModel.setDestinationId(resId);
            mNavController.navigate(R.id.HabitCreateEditFragment);
        });

        mReorderFab.setVisibility(View.VISIBLE);
        mReorderFab.setOnClickListener(v -> toggleReorder());
    }

    private void toggleReorder() {
        final ActionBar actionBar = Objects.requireNonNull(((AppCompatActivity) requireActivity())
                .getSupportActionBar());
        this.mReordering = !mReordering;
        actionBar.setDisplayHomeAsUpEnabled(!mReordering);
        if (mReordering) {
            mBinding.reorderFab.setImageResource(R.drawable.ic_baseline_check_circle_24);
            mBinding.newHabit.setVisibility(View.INVISIBLE);
        } else {
            mBinding.reorderFab.setImageResource(R.drawable.ic_baseline_reorder_24);
            mBinding.newHabit.setVisibility(View.VISIBLE);
        }
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
