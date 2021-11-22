package com.github.cmput301f21t44.hellohabits.view.habit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ItemTouchHelper for reordering Habit items in lists
 */
public class HabitTouchHelper extends ItemTouchHelper {
    /**
     * Creates a new ItemTouchHelper with the default HabitTouchCallback
     */
    public HabitTouchHelper() {
        super(new HabitTouchCallback());
    }

    /**
     * ItemTouchHelper callback for the HabitTouchHelper
     */
    static class HabitTouchCallback extends ItemTouchHelper.SimpleCallback {
        public HabitTouchCallback() {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START |
                    ItemTouchHelper.END, 0);
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            // reduce item opacity if selected
            if (viewHolder != null && actionState == ACTION_STATE_DRAG) {
                viewHolder.itemView.setAlpha(0.5f);
            }
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(1.0f);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            HabitAdapter adapter = (HabitAdapter) recyclerView.getAdapter();
            // no adapter attached
            if (adapter == null) return false;

            int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();

            // not moved, although this might not even be called if the item wasn't moved
            if (from == to) return false;

            adapter.notifyItemMoved(from, to);

            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // do nothing
        }
    }
}
