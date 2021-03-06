package com.github.cmput301f21t44.hellohabits.view.social;


import static com.github.cmput301f21t44.hellohabits.model.social.Follow.Status.ACCEPTED;
import static com.github.cmput301f21t44.hellohabits.model.social.Follow.Status.REQUESTED;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.ListUserItemBinding;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;
import com.github.cmput301f21t44.hellohabits.model.social.Follow;
import com.github.cmput301f21t44.hellohabits.model.social.User;
import com.github.cmput301f21t44.hellohabits.view.OnItemClickListener;
import com.github.cmput301f21t44.hellohabits.viewmodel.UserViewModel;

import java.util.Objects;

/**
 * Adapter class for displaying a User in a RecyclerView
 */
public class UserAdapter extends ListAdapter<User, UserAdapter.ViewHolder> {
    private UserViewModel mViewModel;
    private OnItemClickListener<User> mOnClickUser;
    private OnError mErrorCallback;

    /**
     * Constructor for UserAdapter
     *
     * @param diffCallback Callback used for comparing two Users
     */
    public UserAdapter(@NonNull DiffUtil.ItemCallback<User> diffCallback) {
        super(diffCallback);
    }

    /**
     * Creates a new instance of UserAdapter
     *
     * @param viewModel   UserViewModel to be used by the adapter
     * @param onClickUser Callback for when the user body is clicked (navigate)
     * @return a UserAdapter instance
     */
    public static UserAdapter newInstance(UserViewModel viewModel,
                                          OnItemClickListener<User> onClickUser,
                                          OnError errorCallback) {
        UserAdapter adapter = new UserAdapter(new UserDiff());
        adapter.mViewModel = viewModel;
        adapter.mOnClickUser = onClickUser;
        adapter.mErrorCallback = errorCallback;
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
        ThenFunction onSuccess = () -> notifyItemChanged(position);

        OnItemClickListener<User> viewListener = user -> {
            if (user.getFollowingStatus() != ACCEPTED) return;
            mOnClickUser.onItemClick(user);
        };

        OnItemClickListener<User> acceptListener = user -> mViewModel
                .acceptFollow(user.getEmail(), onSuccess, e ->
                        displayError(e, "accept follow request"));

        // Reject button can be reject, or request/cancel follow
        OnItemClickListener<User> rejectListener = user -> {
            if (user.getFollowerStatus() == Follow.Status.REQUESTED) {
                mViewModel.rejectFollow(user.getEmail(), onSuccess, e ->
                        displayError(e, "reject follow request"));
            } else if (user.getFollowingStatus() == Follow.Status.REQUESTED) {
                mViewModel.cancelFollowRequest(user.getEmail(), onSuccess, e ->
                        displayError(e, "cancel follow request"));
            } else {
                mViewModel.requestFollow(user.getEmail(), onSuccess, (e) ->
                        displayError(e, "request follow"));
            }
        };

        holder.bind(current, viewListener, acceptListener, rejectListener);
    }

    /**
     * Displays an error through the callback
     * @param e Exception thrown
     * @param operation Name of failed operation
     */
    private void displayError(Exception e, String operation) {
        mErrorCallback.displayMessage(String.format("Failed to %s: %s", operation,
                e.getLocalizedMessage()));
    }

    interface OnError {
        /**
         * Called when there is an error in the UserViewModel operation
         *
         * @param message String message to display
         */
        void displayMessage(String message);
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
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    // use Objects.equals because these members are nullable
                    Objects.equals(oldItem.getFollowerStatus(), newItem.getFollowerStatus()) &&
                    Objects.equals(oldItem.getFollowingStatus(), newItem.getFollowingStatus());
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
         * Updates the buttons appearance depending on the following/follower status
         *
         * @param followingStatus Status of follow request (this user - follow -> current user)
         * @param followerStatus  Status of follow request (current user - follow -> this user)
         */
        void updateAppearance(Follow.Status followingStatus, Follow.Status followerStatus) {
            mItemBinding.accept.setBackgroundColor(Color.parseColor("#FF6200EE"));
            mItemBinding.reject.setBackgroundColor(Color.parseColor("#FF6200EE"));

            // either accept or reject, don't add option to follow just yet
            if (followerStatus == Follow.Status.REQUESTED) {
                mItemBinding.accept.setText(R.string.accept);
                mItemBinding.reject.setText(R.string.reject);
                return;
            }

            mItemBinding.accept.setVisibility(View.INVISIBLE);
            if (followingStatus == ACCEPTED) {
                // Remove buttons
                mItemBinding.accept.setVisibility(View.INVISIBLE);
                mItemBinding.reject.setVisibility(View.INVISIBLE);
            } else if (followingStatus == REQUESTED) {
                // Cancel Follow Request
                mItemBinding.reject.setText(R.string.requested);
                mItemBinding.reject.setBackgroundColor(Color.parseColor("#CCCCCC"));
            } else {
                // Send Follow Request
                mItemBinding.reject.setText(R.string.follow);
            }
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

            mItemBinding.userBody.setOnClickListener(v -> viewListener.onItemClick(user));
            mItemBinding.accept.setOnClickListener(v -> acceptListener.onItemClick(user));
            mItemBinding.reject.setOnClickListener(v -> rejectListener.onItemClick(user));

            if (user.getFollowingStatus() == ACCEPTED &&
                    user.getFollowerStatus() != Follow.Status.REQUESTED) {
                // Add view habit list to entire view
                mItemBinding.getRoot().setOnClickListener(v -> viewListener.onItemClick(user));
            }

            updateAppearance(user.getFollowingStatus(), user.getFollowerStatus());
        }
    }
}
