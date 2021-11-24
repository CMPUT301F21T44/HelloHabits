package com.github.cmput301f21t44.hellohabits.view.social;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.cmput301f21t44.hellohabits.databinding.ListUserItemBinding;
import com.github.cmput301f21t44.hellohabits.model.social.User;
import com.github.cmput301f21t44.hellohabits.view.OnItemClickListener;

public class UserAdapter extends ListAdapter<User, UserAdapter.ViewHolder> {
    private OnItemClickListener<User> mViewListener;
    private OnItemClickListener<User> mAcceptListener;
    private OnItemClickListener<User> mRejectListener;

    public UserAdapter(@NonNull DiffUtil.ItemCallback<User> diffCallback) {
        super(diffCallback);
    }

    public static UserAdapter newInstance(OnItemClickListener<User> viewListener,
                                          OnItemClickListener<User> acceptListener,
                                          OnItemClickListener<User> rejectListener) {
        UserAdapter adapter = new UserAdapter(new UserDiff());
        adapter.mViewListener = viewListener;
        adapter.mAcceptListener = acceptListener;
        adapter.mRejectListener = rejectListener;
        return adapter;
    }


    /**
     * UserAdapter's Lifecycle onCreateViewHolder method
     *
     * @param parent   RecyclerView parent
     * @param viewType Type of View (unused)
     * @return a new ViewHolder instance for the User item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ListUserItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    /**
     * UserAdapter's Lifecycle onBindViewHolder method
     * <p>
     * Binds the listeners to the User
     *
     * @param holder   ViewHolder for User item
     * @param position Position of User in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User current = getItem(position);
        holder.bind(current, mViewListener, mAcceptListener, mRejectListener);
    }

    /**
     * ItemCallback class for comparing and updating User RecyclerView items
     */
    public static class UserDiff extends DiffUtil.ItemCallback<User> {
        /**
         * Check if two items are the same
         *
         * @param oldItem Old User item to be compared
         * @param newItem New User item to be compared
         * @return whether the two Users are the same
         */
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getEmail().equals(newItem.getEmail());
        }

        /**
         * Check if two items have the same content
         *
         * @param oldItem Old User item to be compared
         * @param newItem New User item to be compared
         * @return whether the two Users have the same contents
         */
        @Override
        public boolean areContentsTheSame(@NonNull User oldItem,
                                          @NonNull User newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    }

    /**
     * ViewHolder class for holding User items
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ListUserItemBinding mItemBinding;

        /**
         * Creates a new ViewHolder with a layout binding
         *
         * @param binding Layout binding to use
         */
        public ViewHolder(ListUserItemBinding binding) {
            super(binding.getRoot());
            this.mItemBinding = binding;
        }

        /**
         * Binds a User to the ViewHolder
         *
         * @param user           User data to bind
         * @param viewListener   Listener callback for when the User body is clicked
         * @param acceptListener Listener callback for when the accept button is clicked
         * @param rejectListener Listener callback for when the reject button is clicked
         */
        void bind(@NonNull final User user,
                  final OnItemClickListener<User> viewListener,
                  final OnItemClickListener<User> acceptListener,
                  final OnItemClickListener<User> rejectListener) {
            mItemBinding.userEmail.setText(user.getEmail());
            mItemBinding.userName.setText(user.getName());
//            user.getFollowerStatus(); // null, Status.REQUESTED, Status.ACCEPTED
//            user.getFollowingStatus(); // null, Status.REQUESTED, Status.ACCEPTED
            mItemBinding.userName.setOnClickListener(v -> viewListener.onItemClick(user));
            mItemBinding.userEmail.setOnClickListener(v -> viewListener.onItemClick(user));
            mItemBinding.accept.setOnClickListener(v -> acceptListener.onItemClick(user));
            mItemBinding.reject.setOnClickListener(v -> rejectListener.onItemClick(user));
        }
    }
}
