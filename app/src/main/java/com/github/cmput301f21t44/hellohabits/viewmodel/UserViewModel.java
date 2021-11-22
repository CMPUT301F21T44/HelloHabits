package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;
import com.github.cmput301f21t44.hellohabits.model.social.User;
import com.github.cmput301f21t44.hellohabits.model.social.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ViewModel class for viewing other users
 */
public class UserViewModel extends ViewModel {
    private final UserRepository mRepository;
    private final LiveData<List<User>> mAllUsers;
    private final MediatorLiveData<List<User>> mFollowers;
    private final MediatorLiveData<List<User>> mFollowing;

    private final MutableLiveData<User> mSelectedUser = new MutableLiveData<>();

    public UserViewModel(UserRepository userRepository) {
        this.mRepository = userRepository;
        this.mAllUsers = mRepository.getAllUsers();
        this.mFollowers = new MediatorLiveData<>();
        this.mFollowing = new MediatorLiveData<>();
        listenToLiveData(mFollowers, u -> u.getFollowerStatus() != null);
        listenToLiveData(mFollowing, u -> u.getFollowingStatus() != null);
    }

    private void listenToLiveData(MediatorLiveData<List<User>> liveData,
                                  UserCallback userCallback) {
        liveData.addSource(mAllUsers, users -> {
            List<User> filteredList = users.stream().filter(userCallback::hasFollow)
                    .collect(Collectors.toList());
            mFollowing.setValue(filteredList);
        });
    }

    public LiveData<User> getSelectedUser() {
        return mSelectedUser;
    }


    public void setSelectedUser(User user) {
        mSelectedUser.setValue(user);
    }

    public LiveData<List<User>> getAllUsers() {
        return mAllUsers;
    }

    public LiveData<List<User>> getFollowers() {
        return mFollowers;
    }

    public LiveData<List<User>> getFollowing() {
        return mFollowing;
    }

    public void requestFollow(String email, ThenFunction successCallback,
                              CatchFunction failCallback) {
        mRepository.requestFollow(email, successCallback, failCallback);
    }

    public void cancelFollowRequest(String email, ThenFunction successCallback,
                                    CatchFunction failCallback) {
        mRepository.cancelFollow(email, successCallback, failCallback);
    }

    public void acceptFollow(String email, ThenFunction successCallback,
                             CatchFunction failCallback) {
        mRepository.acceptFollow(email, successCallback, failCallback);
    }

    public void rejectFollow(String email, ThenFunction successCallback,
                             CatchFunction failCallback) {
        mRepository.rejectFollow(email, successCallback, failCallback);
    }

    interface UserCallback {
        boolean hasFollow(User user);
    }
}
