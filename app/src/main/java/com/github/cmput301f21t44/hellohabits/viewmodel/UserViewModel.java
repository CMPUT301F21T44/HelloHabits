package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;
import com.github.cmput301f21t44.hellohabits.model.social.Follow;
import com.github.cmput301f21t44.hellohabits.model.social.User;
import com.github.cmput301f21t44.hellohabits.model.social.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        listenToLiveData(mFollowers, mRepository.getFollowers());
        listenToLiveData(mFollowing, mRepository.getFollowing());
    }

    private void listenToLiveData(MediatorLiveData<List<User>> liveData,
                                  LiveData<List<Follow>> followLiveData) {
        liveData.addSource(followLiveData, follows -> {
            List<User> users = mAllUsers.getValue();
            if (users == null) return;
            Map<String, User> userMap = new HashMap<>();
            for (User user : users) {
                userMap.put(user.getEmail(), user);
            }
            List<User> userFollows = new ArrayList<>();
            for (Follow follow : follows) {
                userFollows.add(userMap.get(follow.getEmail()));
            }
            liveData.setValue(userFollows);
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
}
