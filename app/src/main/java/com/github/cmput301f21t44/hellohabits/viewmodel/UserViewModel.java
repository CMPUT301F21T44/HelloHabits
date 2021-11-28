package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;
import com.github.cmput301f21t44.hellohabits.model.social.User;
import com.github.cmput301f21t44.hellohabits.model.social.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel class for viewing other users
 */
public class UserViewModel extends ViewModel {
    private final UserRepository mRepository;
    private final LiveData<List<User>> mAllUsers;
    private final MutableLiveData<User> mSelectedUser = new MutableLiveData<>();
    private final MediatorLiveData<List<User>> mFollowers;
    private final MediatorLiveData<List<User>> mFollowing;
    private final LiveData<User> mCurrentUser;

    /**
     * Creates a new instance of UserViewModel
     *
     * @param userRepository Repository for fetching User data
     */
    public UserViewModel(UserRepository userRepository) {
        this.mRepository = userRepository;
        this.mAllUsers = mRepository.getAllUsers();
        this.mFollowers = createUserListMediator(user -> user.getFollowerStatus() != null);
        this.mFollowing = createUserListMediator(user -> user.getFollowingStatus() != null);
        this.mCurrentUser = mRepository.getCurrentUser();
    }

    /**
     * Creates a MediatorLiveData that filters the list of users
     *
     * @param userCallback Predicate callback to see if the user should be added to the list
     * @return LiveData of the filtered user list
     */
    private MediatorLiveData<List<User>> createUserListMediator(UserCallback userCallback) {
        MediatorLiveData<List<User>> userLiveData = new MediatorLiveData<>();
        userLiveData.addSource(mAllUsers, users -> {
            List<User> userList = new ArrayList<>();
            for (User user : users) {
                if (userCallback.belongsInList(user)) {
                    userList.add(user);
                }
            }
            userLiveData.setValue(userList);
        });
        return userLiveData;
    }

    /**
     * Gets the currently selected user for habit viewing
     *
     * @return LiveData of user
     */
    public LiveData<User> getSelectedUser() {
        return mSelectedUser;
    }


    /**
     * Sets the current user for habit viewing
     *
     * @param user User to be viewed
     */
    public void setSelectedUser(User user) {
        mSelectedUser.setValue(user);
    }

    /**
     * Gets all users
     *
     * @return LiveData List of all Users
     */
    public LiveData<List<User>> getAllUsers() {
        return mAllUsers;
    }

    /**
     * Gets all followers
     *
     * @return LiveData List of all followers
     */
    public LiveData<List<User>> getFollowers() {
        return mFollowers;
    }

    /**
     * Gets all followed Users
     *
     * @return LiveData List of all followed users
     */
    public LiveData<List<User>> getFollowing() {
        return mFollowing;
    }

    /**
     * Requests to follow a user
     *
     * @param email           The email of the user to follow
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    public void requestFollow(String email, ThenFunction successCallback,
                              CatchFunction failCallback) {
        mRepository.requestFollow(email, successCallback, failCallback);
    }

    /**
     * Cancels a follow request
     *
     * @param email           The email of the user to cancel following
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    public void cancelFollowRequest(String email, ThenFunction successCallback,
                                    CatchFunction failCallback) {
        mRepository.cancelFollow(email, successCallback, failCallback);
    }

    /**
     * Accepts a user's follow request
     *
     * @param email           The email of the user requesting to follow
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    public void acceptFollow(String email, ThenFunction successCallback,
                             CatchFunction failCallback) {
        mRepository.acceptFollow(email, successCallback, failCallback);
    }

    /**
     * Rejects a user's follow request
     *
     * @param email           The email of the user requesting to follow
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    public void rejectFollow(String email, ThenFunction successCallback,
                             CatchFunction failCallback) {
        mRepository.rejectFollow(email, successCallback, failCallback);
    }

    /**
     * Gets the current logged in user
     *
     * @return LiveData of user
     */
    public LiveData<User> getCurrentUser() {
        return mCurrentUser;
    }

    /**
     * Callback interface for checking if the user should be in the list
     */
    interface UserCallback {
        boolean belongsInList(User user);
    }
}
