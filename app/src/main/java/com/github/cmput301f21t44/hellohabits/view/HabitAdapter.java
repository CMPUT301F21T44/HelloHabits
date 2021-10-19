package com.github.cmput301f21t44.hellohabits.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.cmput301f21t44.hellohabits.databinding.HabitItemBinding;
import com.github.cmput301f21t44.hellohabits.model.Habit;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class HabitAdapter extends ListAdapter<Habit, HabitAdapter.HabitHolder> {
    private final OnItemClickListener<Habit> listener;
    protected HabitAdapter(@NonNull DiffUtil.ItemCallback<Habit> diffCallback, OnItemClickListener<Habit> listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HabitItemBinding itemBinding = HabitItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HabitHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitHolder holder, int position) {
        Habit current = getItem(position);
        holder.bind(current, listener);
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
                    && oldItem.getDateStarted().equals(newItem.getDateStarted());
        }
    }

    protected static class HabitHolder extends RecyclerView.ViewHolder {
        private final HabitItemBinding mItemBinding;
        private static final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("d MMMM, y").withZone(ZoneId.systemDefault());

        HabitHolder(@NonNull HabitItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.mItemBinding = itemBinding;
        }

        void bind(@NonNull final Habit habit, final OnItemClickListener<Habit> listener) {
            mItemBinding.titleView.setText(habit.getTitle());
            mItemBinding.reasonView.setText(habit.getReason());
            String date = formatter.format(habit.getDateStarted());
            mItemBinding.dateStartedView.setText(date);
            mItemBinding.getRoot().setOnClickListener(v-> listener.onItemClick(habit));
        }

    }
}
