package com.github.cmput301f21t44.hellohabits.firebase;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class FSFollow implements FSUser.Follow, FSDocument<FSFollow> {
    public static final FollowCollection FOLLOWER_COLLECTION =
            new FollowCollection("followers");
    public static final FollowCollection FOLLOWING_COLLECTION =
            new FollowCollection("following");
    public static final String STATUS = "status";

    private final String mEmail;
    private final FSUser.Follow.Status mStatus;

    public FSFollow(String email, Status status) {
        this.mEmail = email;
        this.mStatus = status;
    }

    public FSFollow(QueryDocumentSnapshot doc) {
        this(doc.getId(), convertStatus(doc.getString(STATUS)));
    }

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

    /**
     * Nominal type for Follow
     */
    static final class FollowCollection {
        private final String name;

        FollowCollection(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
