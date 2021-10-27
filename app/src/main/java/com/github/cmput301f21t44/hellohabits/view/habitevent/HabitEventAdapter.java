package com.github.cmput301f21t44.hellohabits.view.habitevent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.cmput301f21t44.hellohabits.databinding.ListHabitEventItemBinding;
import com.github.cmput301f21t44.hellohabits.model.HabitEvent;
import com.github.cmput301f21t44.hellohabits.view.OnItemClickListener;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class HabitEventAdapter extends ListAdapter<HabitEvent, HabitEventAdapter.ViewHolder> {
    private OnItemClickListener<HabitEvent> listener;

    public HabitEventAdapter(@NonNull DiffUtil.ItemCallback<HabitEvent> diffCallback) {
        super(diffCallback);
    }

    public static HabitEventAdapter newInstance(OnItemClickListener<HabitEvent> listener) {
        HabitEventAdapter adapter = new HabitEventAdapter(new HabitEventDiff());
        adapter.listener = listener;
        return adapter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListHabitEventItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        HabitEvent current = getItem(position);
        holder.bind(current, listener);
    }

    public static class HabitEventDiff extends DiffUtil.ItemCallback<HabitEvent> {
        @Override
        public boolean areItemsTheSame(@NonNull HabitEvent oldItem, @NonNull HabitEvent newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull HabitEvent oldItem, @NonNull HabitEvent newItem) {
            return oldItem.getComment().equals(newItem.getComment())
                    && Objects.equals(oldItem.getPhotoPath(), newItem.getPhotoPath())
                    && Objects.equals(oldItem.getLocation(), newItem.getLocation());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ListHabitEventItemBinding mItemBinding;

        public ViewHolder(ListHabitEventItemBinding binding) {
            super(binding.getRoot());
            this.mItemBinding = binding;
        }

        void bind(@NonNull final HabitEvent habitEvent, final OnItemClickListener<HabitEvent> listener) {
            if (habitEvent.getPhotoPath() == null) {
                mItemBinding.imageViewPhoto.setVisibility(View.INVISIBLE);
            }
            if (habitEvent.getLocation() == null) {
                mItemBinding.imageViewLocation.setVisibility(View.INVISIBLE);
            }
            mItemBinding.comment.setText(habitEvent.getComment());
            String date = DateTimeFormatter
                    .ofPattern("eee d MMMM, y")
                    .withZone(ZoneId.systemDefault())
                    .format(habitEvent.getDate());
            mItemBinding.dateDenoted.setText(date);
            mItemBinding.getRoot().setOnClickListener(v -> listener.onItemClick(habitEvent));
        }

    }
}