package com.github.cmput301f21t44.hellohabits.firebase;

import androidx.annotation.NonNull;

import com.github.cmput301f21t44.hellohabits.model.social.Follow;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Firestore implementation of Follow
 */
public class FSFollow implements Follow, FSDocument {
    public static final FollowCollection FOLLOWER_COLLECTION =
            new FollowCollection("followers");
    public static final FollowCollection FOLLOWING_COLLECTION =
            new FollowCollection("following");
    public static final String STATUS = "status";

    private final String mEmail;
    private final Follow.Status mStatus;

    /**
     * Create a FSFollow
     *
     * @param email  User Email
     * @param status Following Status
     */
    public FSFollow(String email, Status status) {
        this.mEmail = email;
        this.mStatus = status;
    }

    /**
     * Create a FSFollow from a DocumentSnapshot
     *
     * @param doc Firestore Document
     */
    public FSFollow(DocumentSnapshot doc) {
        this(doc.getId(), convertStatus(doc.getString(STATUS)));
    }

    /**
     * Returns the current status of a follow request in status type
     *
     * @param status string of current status
     * @return returns the converted string
     */
    private static Status convertStatus(String status) {
        // give a default value to status, this totally won't go wrong 🤪
        return status != null ? Status.get(status) : Status.REQUESTED;
    }

    @Override
    public String getEmail() {
        return mEmail;
    }

    @Override
    public Status getStatus() {
        return mStatus;
    }

    @Override
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(STATUS, mStatus.getText());
        return map;
    }

    @Override
    public String getKey() {
        return mEmail;
    }

    @Override
    @NonNull
    public String toString() {
        return String.format("(%s: %s)", getEmail(), getStatus());
    }

    /**
     * Nominal type for Follow
     */
    static final class FollowCollection {
        private final String name;

        private FollowCollection(String name) {
            this.name = name;
        }

        @NonNull
        @Override
        public String toString() {
            return this.name;
        }
    }
}
