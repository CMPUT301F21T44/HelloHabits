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

/**
 * Adapter class for displaying a HabitEvent in a RecyclerView
 */
public class HabitEventAdapter extends ListAdapter<HabitEvent, HabitEventAdapter.ViewHolder> {
    private OnItemClickListener<HabitEvent> mViewListener;
    private OnItemClickListener<HabitEvent> mEditListener;
    private OnItemClickListener<HabitEvent> mDeleteListener;

    /**
     * Constructor for HabitEventAdapter
     *
     * @param diffCallback Callback used for comparing two HabitEvents
     */
    public HabitEventAdapter(@NonNull DiffUtil.ItemCallback<HabitEvent> diffCallback) {
        super(diffCallback);
    }

    /**
     * Creates a new instance of the HabitEventAdapter
     *
     * @param viewListener   Listener callback for when the HabitEvent body is clicked
     * @param editListener   Listener callback for when the edit button is clicked
     * @param deleteListener Listener callback for when the delete button is clicked
     * @return A HabitEventAdapter instance with the listeners
     */
    public static HabitEventAdapter newInstance(OnItemClickListener<HabitEvent> viewListener,
                                                OnItemClickListener<HabitEvent> editListener,
                                                OnItemClickListener<HabitEvent> deleteListener) {
        HabitEventAdapter adapter = new HabitEventAdapter(new HabitEventDiff());
        adapter.mViewListener = viewListener;
        adapter.mEditListener = editListener;
        adapter.mDeleteListener = deleteListener;
        return adapter;
    }

    /**
     * HabitEventAdapter's Lifecycle onCreateViewHolder method
     *
     * @param parent   RecyclerView parent
     * @param viewType Type of View (unused)
     * @return a new ViewHolder instance for the HabitEvent item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListHabitEventItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    /**
     * HabitEventAdapter's Lifecycle onBindViewHolder method
     * <p>
     * Binds the listeners to the HabitEvent
     *
     * @param holder   ViewHolder for HabitEvent item
     * @param position Position of HabitEvent in the list
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        HabitEvent current = getItem(position);
        holder.bind(current, mViewListener, mEditListener, mDeleteListener);
    }

    /**
     * ItemCallback class for comparing and updating HabitEvent RecyclerView items
     */
    public static class HabitEventDiff extends DiffUtil.ItemCallback<HabitEvent> {
        /**
         * Check if two items are the same
         *
         * @param oldItem Old HabitEvent item to be compared
         * @param newItem New HabitEvent item to be compared
         * @return whether the two HabitEvents are the same
         */
        @Override
        public boolean areItemsTheSame(@NonNull HabitEvent oldItem, @NonNull HabitEvent newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        /**
         * Check if two items have the same content
         *
         * @param oldItem Old HabitEvent item to be compared
         * @param newItem New HabitEvent item to be compared
         * @return whether the two HabitEvents have the same contents
         */
        @Override
        public boolean areContentsTheSame(@NonNull HabitEvent oldItem,
                                          @NonNull HabitEvent newItem) {
            return oldItem.getComment().equals(newItem.getComment())
                    && Objects.equals(oldItem.getPhotoPath(), newItem.getPhotoPath())
                    && Objects.equals(oldItem.getLocation(), newItem.getLocation());
        }
    }

    /**
     * ViewHolder class for holding HabitEvent items
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ListHabitEventItemBinding mItemBinding;

        /**
         * Creates a new ViewHolder with a layout binding
         *
         * @param binding Layout binding to use
         */
        public ViewHolder(ListHabitEventItemBinding binding) {
            super(binding.getRoot());
            this.mItemBinding = binding;
        }

        /**
         * Binds a HabitEvent to the ViewHolder
         *
         * @param event          HabitEvent data to bind
         * @param viewListener   Listener callback for when the HabitEvent body is clicked
         * @param editListener   Listener callback for when the edit button is clicked
         * @param deleteListener Listener callback for when the delete button is clicked
         */
        void bind(@NonNull final HabitEvent event,
                  final OnItemClickListener<HabitEvent> viewListener,
                  final OnItemClickListener<HabitEvent> editListener,
                  final OnItemClickListener<HabitEvent> deleteListener) {
            mItemBinding.comment.setText(event.getComment());

            // set Date
            String date = DateTimeFormatter.ofPattern("d/M/y").withZone(ZoneId.systemDefault())
                    .format(event.getDate());
            mItemBinding.dateDenoted.setText(date);

            // bind listeners
            mItemBinding.textHabitEvent.setOnClickListener(v -> viewListener.onItemClick(event));
            mItemBinding.buttonEdit.setOnClickListener(v -> editListener.onItemClick(event));
            mItemBinding.buttonDelete.setOnClickListener(v -> deleteListener.onItemClick(event));
        }
    }
}