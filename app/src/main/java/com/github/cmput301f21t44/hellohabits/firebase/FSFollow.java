package com.github.cmput301f21t44.hellohabits.firebase;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class FSFollow implements FSUser.Follow {
    public static final String FOLLOWER_COLLECTION = "followers";
    public static final String FOLLOWING_COLLECTION = "following";
    public static final String STATUS = "status";

    private final String mEmail;
    private final FSUser.Follow.Status mStatus;

    public FSFollow(String email, Status status) {
        this.mEmail = email;
        this.mStatus = status;
    }

    public static FSFollow fromSnapshot(QueryDocumentSnapshot doc) {
        String email = doc.getId();
        String statusStr = doc.getString(STATUS);
        // give a default value to status, this totally won't go wrong 🤪
        Status status = Status.get(statusStr != null ? statusStr : String.valueOf(Status.REQUESTED));
        return new FSFollow(email, status);
    }

    public static Map<String, Object> getMap(FSFollow follow) {
        Map<String, Object> map = new HashMap<>();
        map.put(STATUS, follow.getStatus().getText());
        return map;
    }

    @Override
    public String getEmail() {
        return mEmail;
    }

    @Override
    public Status getStatus() {
        return mStatus;
    }

}
