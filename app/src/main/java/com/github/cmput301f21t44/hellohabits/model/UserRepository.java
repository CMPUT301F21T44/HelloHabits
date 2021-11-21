package com.github.cmput301f21t44.hellohabits.model;

import androidx.lifecycle.LiveData;

import com.github.cmput301f21t44.hellohabits.firebase.CatchFunction;
import com.github.cmput301f21t44.hellohabits.firebase.ThenFunction;

import java.util.List;

/**
 * Interface to implement user repository methods
 */
public interface UserRepository {
    /**
     * Get list of users that follow the current user
     *
     * @return List of followers
     */
    LiveData<List<Follow>> getAllFollowers();

    /**
     * Get list of users that the current user follows
     *
     * @return List of users being followed by the current user
     */
    LiveData<List<Follow>> getAllFollowing();

    /**
     * Get a user's info
     *
     * @param email The user's email
     */
    LiveData<User> getUser(String email);

    /**
     * Requests to follow a user
     *
     * @param email           The email of the user to follow
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void requestFollow(String email, ThenFunction successCallback, CatchFunction failCallback);

    /**
     * Cancels a follow request
     *
     * @param email           The email of the user to cancel following
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void cancelFollow(String email, ThenFunction successCallback, CatchFunction failCallback);

    /**
     * Accepts a user's follow request
     *
     * @param email           The email of the user requesting to follow
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void acceptFollow(String email, ThenFunction successCallback, CatchFunction failCallback);

    /**
     * Rejects a user's follow request
     *
     * @param email           The email of the user requesting to follow
     * @param successCallback Callback for when the operation succeeds
     * @param failCallback    Callback for when the operation fails
     */
    void rejectFollow(String email, ThenFunction successCallback, CatchFunction failCallback);
}
