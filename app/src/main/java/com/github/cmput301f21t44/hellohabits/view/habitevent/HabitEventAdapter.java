package com.github.cmput301f21t44.hellohabits.view.habitevent;

import android.view.LayoutInflater;
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
    private OnItemClickListener<HabitEvent> viewListener;
    private OnItemClickListener<HabitEvent> editListener;
    private OnItemClickListener<HabitEvent> deleteListener;

    /**
     * @param diffCallback
     */
    public HabitEventAdapter(@NonNull DiffUtil.ItemCallback<HabitEvent> diffCallback) {
        super(diffCallback);
    }

    /**
     * @param viewListener
     * @param editListener
     * @param deleteListener
     * @return
     */
    public static HabitEventAdapter newInstance(OnItemClickListener<HabitEvent> viewListener,
                                                OnItemClickListener<HabitEvent> editListener,
                                                OnItemClickListener<HabitEvent> deleteListener) {
        HabitEventAdapter adapter = new HabitEventAdapter(new HabitEventDiff());
        adapter.viewListener = viewListener;
        adapter.editListener = editListener;
        adapter.deleteListener = deleteListener;
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
        return new ViewHolder(ListHabitEventItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    /**
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        HabitEvent current = getItem(position);
        holder.bind(current, viewListener, editListener, deleteListener);
    }

    public static class HabitEventDiff extends DiffUtil.ItemCallback<HabitEvent> {
        /**
         * @param oldItem
         * @param newItem
         * @return
         */
        @Override
        public boolean areItemsTheSame(@NonNull HabitEvent oldItem, @NonNull HabitEvent newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        /**
         * @param oldItem
         * @param newItem
         * @return
         */
        @Override
        public boolean areContentsTheSame(@NonNull HabitEvent oldItem,
                                          @NonNull HabitEvent newItem) {
            return oldItem.getComment().equals(newItem.getComment())
                    && Objects.equals(oldItem.getPhotoPath(), newItem.getPhotoPath())
                    && Objects.equals(oldItem.getLocation(), newItem.getLocation());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ListHabitEventItemBinding mItemBinding;

        /**
         * @param binding
         */
        public ViewHolder(ListHabitEventItemBinding binding) {
            super(binding.getRoot());
            this.mItemBinding = binding;
        }

        /**
         * @param habitEvent
         * @param viewListener
         * @param editListener
         * @param deleteListener
         */
        void bind(@NonNull final HabitEvent habitEvent,
                  final OnItemClickListener<HabitEvent> viewListener,
                  final OnItemClickListener<HabitEvent> editListener,
                  final OnItemClickListener<HabitEvent> deleteListener) {
            mItemBinding.comment.setText(habitEvent.getComment());
            String date = DateTimeFormatter
                    .ofPattern("d/M/y")
                    .withZone(ZoneId.systemDefault())
                    .format(habitEvent.getDate());
            mItemBinding.dateDenoted.setText(date);
            mItemBinding.textHabitEvent.setOnClickListener(v ->
                    viewListener.onItemClick(habitEvent));
            mItemBinding.buttonEdit.setOnClickListener(v -> editListener.onItemClick(habitEvent));
            mItemBinding.buttonDelete.setOnClickListener(v ->
                    deleteListener.onItemClick(habitEvent));
        }
    }
}