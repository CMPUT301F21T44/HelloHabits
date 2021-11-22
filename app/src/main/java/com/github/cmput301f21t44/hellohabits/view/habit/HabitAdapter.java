package com.github.cmput301f21t44.hellohabits.view.habit;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.cmput301f21t44.hellohabits.databinding.ListHabitItemBinding;
import com.github.cmput301f21t44.hellohabits.model.habit.Habit;
import com.github.cmput301f21t44.hellohabits.view.OnItemClickListener;

/**
 * Adapter class for displaying a Habitin a RecyclerView
 */
public class HabitAdapter extends ListAdapter<Habit, HabitAdapter.ViewHolder> {
    public static int COLOUR_RED = Color.parseColor("#D12D2D");
    public static int COLOUR_ORANGE = Color.parseColor("#EFB112");
    public static int COLOUR_GREEN = Color.parseColor("#329548");

    private OnItemClickListener<Habit> mOnClickListener;

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
     * @param listener Listener callback for when the Habit body is clicked
     * @return A HabitAdapter instance with the listener
     */
    public static HabitAdapter newInstance(OnItemClickListener<Habit> listener) {
        HabitAdapter adapter = new HabitAdapter(new HabitDiff());
        adapter.mOnClickListener = listener;
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
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit current = getItem(position);
        holder.bind(current, mOnClickListener);
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
                    && oldItem.getEvents().equals(newItem.getEvents())
                    && oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getDateStarted().equals(newItem.getDateStarted())
                    && oldItem.getIndex() == newItem.getIndex()
                    && Habit.getConsistency(oldItem) == Habit.getConsistency(oldItem);
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
        void bind(@NonNull final Habit habit, final OnItemClickListener<Habit> listener) {
            mItemBinding.titleView.setText(habit.getTitle());
            mItemBinding.reasonView.setText(habit.getReason());
            // check level of consistency
            double consistency = Habit.getConsistency(habit);

            if (consistency < 0.5) {
                mItemBinding.imageView.setColorFilter(COLOUR_RED);
            } else if (consistency < 0.75) {
                mItemBinding.imageView.setColorFilter(COLOUR_ORANGE);
            } else {
                mItemBinding.imageView.setColorFilter(COLOUR_GREEN);
            }

            mItemBinding.lock.setVisibility(habit.isPrivate() ? VISIBLE : INVISIBLE);
            mItemBinding.getRoot().setOnClickListener(v -> listener.onItemClick(habit));
            HabitIndexChange.setTags(mItemBinding.getRoot(), habit);
        }
    }
}
