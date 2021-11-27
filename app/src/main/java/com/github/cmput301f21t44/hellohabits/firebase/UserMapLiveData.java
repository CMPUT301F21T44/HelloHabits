package com.github.cmput301f21t44.hellohabits.firebase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.github.cmput301f21t44.hellohabits.model.social.Follow;

import java.util.HashMap;
import java.util.Map;

/**
 * LiveData of user emails mapped to other user data
 * <p>
 * Mediates LiveData of following and follower status to update map of users
 */
class UserMapLiveData extends MediatorLiveData<Map<String, FSUser>> {
    private final LiveData<Map<String, Follow.Status>> mFollowerStatus;
    private final LiveData<Map<String, Follow.Status>> mFollowingStatus;

    /**
     * Creates a new instance of UserMapLiveData
     *
     * @param followerStatus  LiveData Map of follower status
     * @param followingStatus LiveData Map of following status
     */
    public UserMapLiveData(LiveData<Map<String, Follow.Status>> followerStatus,
                           LiveData<Map<String, Follow.Status>> followingStatus) {
        this.mFollowingStatus = followingStatus;
        this.mFollowerStatus = followerStatus;
    }

    /**
     * Merge an updated user map with the old one
     * <p>
     * Called when there are new users or if an existing user changed their email
     *
     * @param userMap Updated FSUser map
     */
    public void mergeMap(Map<String, FSUser> userMap) {
        Map<String, FSUser> oldMap = this.getValue();
        // Create a new map and start observing the following and follower LiveData
        if (oldMap == null) {
            this.setValue(userMap);
            addFollowLiveData(mFollowerStatus, FSUser::setFollowerStatus);
            addFollowLiveData(mFollowingStatus, FSUser::setFollowingStatus);
            return;
        }

        // Update old map
        Map<String, FSUser> newMap = new HashMap<>();
        for (FSUser user : userMap.values()) {
            FSUser oldUser = oldMap.get(user.getEmail());
            // merge old user's follow status
            if (oldUser != null) {
                user.setFollowingStatus(oldUser.getFollowingStatus());
                user.setFollowerStatus(oldUser.getFollowerStatus());
            }

            newMap.put(user.getEmail(), user);
        }

        this.setValue(newMap);
    }

    /**
     * Observes a LiveData Map of Follow status
     *
     * @param followLiveData LiveData map to be observed by this MediatorLiveData
     * @param update         Callback to update the user status
     */
    private void addFollowLiveData(LiveData<Map<String, Follow.Status>> followLiveData,
                                   StatusUpdate update) {
        this.addSource(followLiveData, following -> {
            if (following == null) return;
            final Map<String, FSUser> users = this.getValue();
            final Map<String, FSUser> updatedUsers = new HashMap<>();
            if (users == null || users.isEmpty()) return;
            for (String userKey : users.keySet()) {
                FSUser user = users.get(userKey);
                if (user == null) return;
                Follow.Status status = following.get(userKey);
                update.apply(user, status);
                updatedUsers.put(userKey, user);
            }

            this.setValue(updatedUsers);
        });
    }

    /**
     * Interface to apply updates to follow status on a user
     */
    interface StatusUpdate {
        /**
         * Apply an update to the user follow status
         *
         * @param user   User to update
         * @param status new follow status
         */
        void apply(FSUser user, Follow.Status status);
    }
}
