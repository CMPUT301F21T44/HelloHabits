package com.github.cmput301f21t44.hellohabits.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;
import com.github.cmput301f21t44.hellohabits.model.social.Follow;
import com.github.cmput301f21t44.hellohabits.model.social.User;
import com.github.cmput301f21t44.hellohabits.model.social.UserRepository;

import java.util.List;

/**
 * ViewModel class for viewing other users
 */
public class UserViewModel extends ViewModel {
    private final UserRepository mRepository;
    private final LiveData<List<Follow>> mFollowers;
    private final LiveData<List<Follow>> mFollowing;

    private LiveData<User> mSelectedUser = new MutableLiveData<>();

    public UserViewModel(UserRepository userRepository) {
        this.mRepository = userRepository;
        this.mFollowers = mRepository.getAllFollowers();
        this.mFollowing = mRepository.getAllFollowing();
    }

    public LiveData<User> getSelectedUser() {
        return mSelectedUser;
    }

    public void setSelectedUser(String email) {
        mSelectedUser = mRepository.getUser(email);
    }

    public LiveData<List<Follow>> getFollowers() {
        return mFollowers;
    }

    public LiveData<List<Follow>> getFollowing() {
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
