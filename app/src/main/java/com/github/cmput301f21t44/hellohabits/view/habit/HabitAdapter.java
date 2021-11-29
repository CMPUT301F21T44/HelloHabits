package com.github.cmput301f21t44.hellohabits.view.habit;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.ListHabitItemBinding;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;
import com.github.cmput301f21t44.hellohabits.view.OnItemClickListener;
import com.github.cmput301f21t44.hellohabits.viewmodel.HabitViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Adapter class for displaying a Habit in a RecyclerView
 */
public class HabitAdapter extends ListAdapter<Habit, HabitAdapter.ViewHolder> {
    public static int COLOUR_RED = Color.parseColor("#D12D2D");
    public static int COLOUR_ORANGE = Color.parseColor("#EFB112");
    public static int COLOUR_GREEN = Color.parseColor("#329548");

    private OnItemClickListener<Habit> mOnClickListener;
    private OnStartDragListener mDragStartListener;
    private HabitViewModel mViewModel;
    private LifecycleOwner mLifeCycleOwner;
    private GetHabitList mHabitListGetter;

    /**
     * Constructor for HabitAdapter
     *
     * @param diffCallback Callback used for comparing two Habits
     */
    public HabitAdapter(@NonNull DiffUtil.ItemCallback<Habit> diffCallback) {
        super(diffCallback);
    }

    /**
     * Creates a new instance of the HabitAdapter
     *
     * @param clickListener     OnItemClickListener callback for when the Habit body is clicked
     * @param dragStartListener OnStartDragListener callback for when the Habit item handle is dragged
     * @param viewModel         HabitViewModel for observing reordering state
     * @param lifecycleOwner    LifeCycleOwner of the Fragment that this adapter is instantiated in
     * @param habitListGetter   Callback for getting the habit list
     * @return A HabitAdapter instance with the listeners
     */
    public static HabitAdapter newInstance(OnItemClickListener<Habit> clickListener,
                                           OnStartDragListener dragStartListener,
                                           HabitViewModel viewModel, LifecycleOwner lifecycleOwner,
                                           GetHabitList habitListGetter
    ) {
        HabitAdapter adapter = new HabitAdapter(new HabitDiff());
        adapter.mOnClickListener = clickListener;
        adapter.mDragStartListener = dragStartListener;
        adapter.mViewModel = viewModel;
        adapter.mLifeCycleOwner = lifecycleOwner;
        adapter.mHabitListGetter = habitListGetter;
        return adapter;
    }

    /**
     * HabitAdapter's Lifecycle onCreateViewHolder method
     *
     * @param parent   RecyclerView parent
     * @param viewType Type of View (unused)
     * @return a new ViewHolder instance for the Habit item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListHabitItemBinding itemBinding = ListHabitItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    /**
     * HabitAdapter's Lifecycle onBindViewHolder method
     * <p>
     * Binds the listener to the Habit
     *
     * @param holder   ViewHolder for Habit item
     * @param position Position of Habit in the list
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit current = getItem(position);
        holder.bind(current, mOnClickListener);

        // don't try to observe a null ViewModel
        if (mViewModel == null) return;

        // change lock icon depending on the reordering state
        mViewModel.getReordering().observe(mLifeCycleOwner, isReordering -> {
            ImageView handle = holder.mItemBinding.lock;
            handle.setVisibility(isReordering || current.isPrivate() ? VISIBLE : INVISIBLE);
            if (isReordering) {
                handle.setOnTouchListener((v, event) -> {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                });
                handle.setImageResource(R.drawable.ic_baseline_reorder_24);
            } else {
                handle.setOnTouchListener(null);
                handle.setImageResource(R.drawable.ic_baseline_lock_24);
            }
        });
        // for resetting the consistencies
        mHabitListGetter.getHabits().observe(mLifeCycleOwner, habits -> {
            Collections.sort(habits, Comparator.comparingInt(Habit::getIndex));
            for (Habit h : habits) {
                if (h.getId().equals(current.getId())) {
                    double consistency = Habit.getConsistency(h);
                    if (consistency < 0.5) {
                        holder.mItemBinding.imageView.setColorFilter(COLOUR_RED);
                    } else if (consistency < 0.75) {
                        holder.mItemBinding.imageView.setColorFilter(COLOUR_ORANGE);
                    } else {
                        holder.mItemBinding.imageView.setColorFilter(COLOUR_GREEN);
                    }
                }
            }
            submitList(habits);
        });
    }

    interface GetHabitList {
        /**
         * Called to get the LiveData List of Habits
         *
         * @return
         */
        LiveData<List<Habit>> getHabits();
    }

    interface OnStartDragListener {
        /**
         * Called when a view is requesting a start of a drag.
         *
         * @param viewHolder The holder of the view to drag.
         */
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    /**
     * ItemCallback class for comparing and updating Habit RecyclerView items
     */
    public static class HabitDiff extends DiffUtil.ItemCallback<Habit> {
        /**
         * Check if two items are the same
         *
         * @param oldItem Old Habit item to be compared
         * @param newItem New Habit item to be compared
         * @return whether the two Habits are the same
         */
        @Override
        public boolean areItemsTheSame(@NonNull Habit oldItem, @NonNull Habit newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        /**
         * Check if two items have the same content
         *
         * @param oldItem Old Habit item to be compared
         * @param newItem New Habit item to be compared
         * @return whether the two Habits have the same contents
         */
        @Override
        public boolean areContentsTheSame(@NonNull Habit oldItem, @NonNull Habit newItem) {
            return oldItem.getReason().equals(newItem.getReason())
                    && Objects.equals(oldItem.getEvents(), newItem.getEvents())
                    && oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getDateStarted().equals(newItem.getDateStarted())
                    && oldItem.getIndex() == newItem.getIndex()
                    && Habit.getConsistency(oldItem) == Habit.getConsistency(newItem);
        }
    }

    /**
     * ViewHolder class for holding Habit items
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final ListHabitItemBinding mItemBinding;

        /**
         * Creates a new ViewHolder with a layout binding
         *
         * @param itemBinding Layout binding to use
         */
        ViewHolder(@NonNull ListHabitItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.mItemBinding = itemBinding;
        }

        /**
         * Binds a Habit to the ViewHolder
         *
         * @param habit    Habit data to bind
         * @param listener Listener callback for when the Habit body is clicked
         */
        void bind(@NonNull Habit habit, final OnItemClickListener<Habit> listener) {
            mItemBinding.titleView.setText(habit.getTitle());
            mItemBinding.reasonView.setText(habit.getReason());
            mItemBinding.lock.setVisibility(INVISIBLE);
            // check level of consistency
            double consistency = Habit.getConsistency(habit);
            if (consistency < 0.5) {
                mItemBinding.imageView.setColorFilter(COLOUR_RED);
            } else if (consistency < 0.75) {
                mItemBinding.imageView.setColorFilter(COLOUR_ORANGE);
            } else {
                mItemBinding.imageView.setColorFilter(COLOUR_GREEN);
            }

            mItemBinding.getRoot().setOnClickListener(v -> listener.onItemClick(habit));
            HabitIndexChange.setTags(mItemBinding.getRoot(), habit);
        }
    }
}
