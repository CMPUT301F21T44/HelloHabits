package com.github.cmput301f21t44.hellohabits.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.LlistHabitItemBinding;
import com.github.cmput301f21t44.hellohabits.model.Habit;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.Set;

public class HabitAdapter extends ListAdapter<Habit, HabitAdapter.HabitHolder> {
    private final OnItemClickListener<Habit> listener;
    protected HabitAdapter(@NonNull DiffUtil.ItemCallback<Habit> diffCallback, OnItemClickListener<Habit> listener) {
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
        private static final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("d MMMM, y").withZone(ZoneId.systemDefault());

        HabitHolder(@NonNull LlistHabitItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.mItemBinding = itemBinding;
        }

        void bind(@NonNull final Habit habit, final OnItemClickListener<Habit> listener) {
            mItemBinding.titleView.setText(habit.getTitle());
            mItemBinding.reasonView.setText(habit.getReason());
            // check level of consistency
            Instant instant = habit.getDateStarted();
            LocalDate startDate = instant.atZone(ZoneId.of( "America/Edmonton" )).toLocalDate();
            LocalDate endDate = LocalDate.now();
            int startDay = startDate.getDayOfWeek().getValue();
            int endDay = endDate.getDayOfWeek().getValue();
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            boolean recurranceDays[] = habit.getDaysOfWeek();
            int numRecurranceDays = 0;
            for (int i = 0;i<recurranceDays.length;i++){
                if(recurranceDays[i]){
                    numRecurranceDays++;
                }
            }
            long totalDays = 0;
            for (int i = startDay;i<=7;i++){
                if (recurranceDays[i-1]){
                    totalDays++;
                }
                days--;
            }
            for(int i = endDay;i>=1;i--){
                if (recurranceDays[i-1]){
                    totalDays++;
                }
                days--;
            }
            totalDays = totalDays + numRecurranceDays*days/7;
            double consistency = 1;
            if(totalDays!=0) {
                consistency = habit.getEvents().size() / totalDays;
            }
            // if consistency <50% red logo, if between 50 and 75%, yellow logo,
            // if greater than 75% green logo.
            // if habit is non repetitive or hasnt started yet, consistency = 100%
            if(consistency<0.5){
                mItemBinding.imageView.setImageResource(R.mipmap.red_logo_round);
            }
            else if(consistency<0.75){
                mItemBinding.imageView.setImageResource(R.mipmap.yellow_logo_round);
            }
            else{
                mItemBinding.imageView.setImageResource(R.mipmap.green_logo_round);
            }

            mItemBinding.getRoot().setOnClickListener(v-> listener.onItemClick(habit));
        }

    }
}
