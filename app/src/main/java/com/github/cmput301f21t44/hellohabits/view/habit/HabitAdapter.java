package com.github.cmput301f21t44.hellohabits.view.habit;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.cmput301f21t44.hellohabits.databinding.ListHabitItemBinding;
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.view.OnItemClickListener;

public class HabitAdapter extends ListAdapter<Habit, HabitAdapter.ViewHolder> {
    private OnItemClickListener<Habit> listener;

    /**
     * @param diffCallback
     */
    public HabitAdapter(@NonNull DiffUtil.ItemCallback<Habit> diffCallback) {
        super(diffCallback);
    }

    /**
     * @param listener
     * @return
     */
    public static HabitAdapter newInstance(OnItemClickListener<Habit> listener) {
        HabitAdapter adapter = new HabitAdapter(new HabitDiff());
        adapter.listener = listener;
        return adapter;
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListHabitItemBinding itemBinding = ListHabitItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    /**
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit current = getItem(position);
        holder.bind(current, listener);
    }

    public static class HabitDiff extends DiffUtil.ItemCallback<Habit> {
        /**
         * This function tells if two habit items are same by ID
         *
         * @param oldItem the old habit
         * @param newItem the new habit
         * @return a boolean value for equality: true for equal, false for unequal
         */
        @Override
        public boolean areItemsTheSame(@NonNull Habit oldItem, @NonNull Habit newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        /**
         * This function tells if two habit items are same by all the contents
         *
         * @param oldItem the old habit
         * @param newItem the new habit
         * @return a boolean value for equality: true for equal, false for unequal
         */
        @Override
        public boolean areContentsTheSame(@NonNull Habit oldItem, @NonNull Habit newItem) {
            return oldItem.getReason().equals(newItem.getReason())
                    && oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getDateStarted().equals(newItem.getDateStarted())
                    && Habit.getConsistency(oldItem) == Habit.getConsistency(oldItem);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final ListHabitItemBinding mItemBinding;

        /**
         * @param itemBinding
         */
        ViewHolder(@NonNull ListHabitItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.mItemBinding = itemBinding;
        }

        /**
         * @param habit
         * @param listener
         */
        void bind(@NonNull final Habit habit, final OnItemClickListener<Habit> listener) {
            mItemBinding.titleView.setText(habit.getTitle());
            mItemBinding.reasonView.setText(habit.getReason());
            // check level of consistency
            double consistency = Habit.getConsistency(habit);

            if (consistency < 0.5) {
                mItemBinding.imageView.setColorFilter(Color.parseColor("#D12D2D"));
            } else if (consistency < 0.75) {
                mItemBinding.imageView.setColorFilter(Color.parseColor("#EFB112"));
            } else {
                mItemBinding.imageView.setColorFilter(Color.parseColor("#329548"));
            }

            mItemBinding.getRoot().setOnClickListener(v -> listener.onItemClick(habit));
        }

    }
}
