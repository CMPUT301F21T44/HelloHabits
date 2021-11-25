package com.github.cmput301f21t44.hellohabits.view.social;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.databinding.ListUserItemBinding;
import com.github.cmput301f21t44.hellohabits.model.social.Follow;
import com.github.cmput301f21t44.hellohabits.model.social.User;
import com.github.cmput301f21t44.hellohabits.view.OnItemClickListener;
import com.github.cmput301f21t44.hellohabits.viewmodel.UserViewModel;

public class UserAdapter extends ListAdapter<User, UserAdapter.ViewHolder> {
    private LifecycleOwner mlifeCycleOwner;
    private UserViewModel mViewModel;
    private OnItemClickListener<User> mOnClickUser;

    public UserAdapter(@NonNull DiffUtil.ItemCallback<User> diffCallback) {
        super(diffCallback);
    }

    /**
     * Creates a new instance of UserAdapter
     *
     * @param lifecycleOwner Lifecycle owner of the fragment
     * @param viewModel      UserViewModel to be used by the adapter
     * @param onClickUser    Callback for when the user body is clicked (navigate)
     * @return a UserAdapter instance
     */
    public static UserAdapter newInstance(LifecycleOwner lifecycleOwner, UserViewModel viewModel,
                                          OnItemClickListener<User> onClickUser) {
        UserAdapter adapter = new UserAdapter(new UserDiff());
        adapter.mlifeCycleOwner = lifecycleOwner;
        adapter.mViewModel = viewModel;
        adapter.mOnClickUser = onClickUser;
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
        holder.bind(current, user -> {
            if (user.getFollowingStatus() != Follow.Status.ACCEPTED) return;
            mOnClickUser.onItemClick(user);
        }, user -> {
            // accept button
            mViewModel.acceptFollow(user.getEmail(), () -> {
            }, (e) -> {
            });
        }, user -> {
            // reject button
            if (user.getFollowerStatus() == Follow.Status.REQUESTED) {
                mViewModel.rejectFollow(user.getEmail(), () -> {
                }, (e) -> {
                });
            } else {
                mViewModel.requestFollow(user.getEmail(), () -> {
                }, (e) -> {
                });
            }
        });
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

            mItemBinding.userBody.setOnClickListener(v -> viewListener.onItemClick(user));
            mItemBinding.accept.setOnClickListener(v -> acceptListener.onItemClick(user));
            mItemBinding.reject.setOnClickListener(v -> rejectListener.onItemClick(user));

            // either accept or reject, don't add option to follow just yet
            if (user.getFollowerStatus() == Follow.Status.REQUESTED) {
                mItemBinding.accept.setText(R.string.accept);
                mItemBinding.reject.setText(R.string.reject);
                return;
            }


            mItemBinding.accept.setVisibility(View.INVISIBLE);
            Follow.Status status = user.getFollowingStatus();
            // add option to follow
            if (status == null) {
                mItemBinding.reject.setText(R.string.follow);
            } else if (status == Follow.Status.REQUESTED) {
                // all buttons are gone
                mItemBinding.reject.setText(R.string.requested);
                mItemBinding.reject.setBackgroundColor(Color.parseColor("#CCCCCC"));
                mItemBinding.reject.setEnabled(false);
            } else if (status == Follow.Status.ACCEPTED) {
                mItemBinding.accept.setVisibility(View.INVISIBLE);
                mItemBinding.reject.setVisibility(View.INVISIBLE);
            }
        }
    }
}
