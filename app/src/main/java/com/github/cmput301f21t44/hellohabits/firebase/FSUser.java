package com.github.cmput301f21t44.hellohabits.firebase;

import androidx.annotation.NonNull;

import com.github.cmput301f21t44.hellohabits.model.social.Follow;
import com.github.cmput301f21t44.hellohabits.model.social.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Firestore implementation of User
 */
public class FSUser implements User, FSDocument {
    public static final String COLLECTION = "users";
    public static final String NAME = "name";
    private final String mEmail;
    private final String mName;

    private Follow.Status mFollowerStatus;
    private Follow.Status mFollowingStatus;

    /**
     * Create a FSUser
     *
     * @param email User's email
     * @param name  User's Name
     */
    public FSUser(String email, String name) {
        this.mEmail = email;
        this.mName = name;
    }

    /**
     * Create a FSUser using a DocumentSnapshot
     *
     * @param doc Firestore DocumentSnapshot
     */
    public FSUser(DocumentSnapshot doc) {
        this(doc.getId(), doc.getString(NAME));
    }


    /**
     * @return The user's email address
     */
    @Override
    public String getEmail() {
        return mEmail;
    }

    /**
     * @return The user's public name
     */
    @Override
    public String getName() {
        return mName;
    }

    @Override
    public Follow.Status getFollowingStatus() {
        return mFollowingStatus;
    }

    public void setFollowingStatus(Follow.Status mFollowingStatus) {
        this.mFollowingStatus = mFollowingStatus;
    }

    @Override
    public Follow.Status getFollowerStatus() {
        return mFollowerStatus;
    }

    public void setFollowerStatus(Follow.Status mFollowerStatus) {
        this.mFollowerStatus = mFollowerStatus;
    }

    @Override
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(NAME, getName());
        return map;
    }

    @Override
    public String getKey() {
        return mEmail;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s %s %s %s", mName, mEmail,
                Objects.toString(mFollowerStatus, "null"),
                Objects.toString(mFollowingStatus, "null"));
    }
}
