package com.github.cmput301f21t44.hellohabits.view.habit;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.LlistHabitItemBinding;
import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.view.OnItemClickListener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class HabitAdapter extends ListAdapter<Habit, HabitAdapter.HabitHolder> {
    private final OnItemClickListener<Habit> listener;

    public HabitAdapter(@NonNull DiffUtil.ItemCallback<Habit> diffCallback, OnItemClickListener<Habit> listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LlistHabitItemBinding itemBinding = LlistHabitItemBinding.inflate(
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
        private final LlistHabitItemBinding mItemBinding;

        HabitHolder(@NonNull LlistHabitItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.mItemBinding = itemBinding;
        }

        void bind(@NonNull final Habit habit, final OnItemClickListener<Habit> listener) {
            mItemBinding.titleView.setText(habit.getTitle());
            mItemBinding.reasonView.setText(habit.getReason());
            // check level of consistency
            double consistency = Habit.getConsistency(habit);

            if (consistency < 0.5) {
                mItemBinding.imageView.setImageResource(R.mipmap.red_logo_round);
            } else if (consistency < 0.75) {
                mItemBinding.imageView.setImageResource(R.mipmap.yellow_logo_round);
            } else {
                mItemBinding.imageView.setImageResource(R.mipmap.green_logo_round);
            }

            mItemBinding.getRoot().setOnClickListener(v -> listener.onItemClick(habit));
        }

    }
}
