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
    public static int COLOUR_RED = Color.parseColor("#D12D2D");
    public static int COLOUR_ORANGE = Color.parseColor("#EFB112");
    public static int COLOUR_GREEN = Color.parseColor("#329548");

    private OnItemClickListener<Habit> mOnClickListener;

    public HabitAdapter(@NonNull DiffUtil.ItemCallback<Habit> diffCallback) {
        super(diffCallback);
    }

    public static HabitAdapter newInstance(OnItemClickListener<Habit> listener) {
        HabitAdapter adapter = new HabitAdapter(new HabitDiff());
        adapter.mOnClickListener = listener;
        return adapter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListHabitItemBinding itemBinding = ListHabitItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit current = getItem(position);
        holder.bind(current, mOnClickListener);
    }

    public static class HabitDiff extends DiffUtil.ItemCallback<Habit> {
        @Override
        public boolean areItemsTheSame(@NonNull Habit oldItem, @NonNull Habit newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

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

        ViewHolder(@NonNull ListHabitItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.mItemBinding = itemBinding;
        }

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

            mItemBinding.getRoot().setOnClickListener(v -> listener.onItemClick(habit));
        }

    }
}
