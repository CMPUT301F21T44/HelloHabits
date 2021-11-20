package com.github.cmput301f21t44.hellohabits.firebase;

import com.github.cmput301f21t44.hellohabits.model.Habit;
import com.github.cmput301f21t44.hellohabits.model.User;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FSUser implements User {
    public static final String COLLECTION = "users";
    public static final String NAME = "name";
    private final String mEmail;
    private final String mName;
    private List<Habit> mHabits;
    private List<Follow> mFollowing;
    private List<Follow> mFollowers;

    public FSUser(String email, String name) {
        this.mEmail = email;
        this.mName = name;
    }

    public static FSUser fromSnapshot(QueryDocumentSnapshot doc) {
        String email = doc.getId();
        String name = doc.getString(NAME);
        return new FSUser(email, name);
    }

    public static Map<String, Object> getMap(FSUser user) {
        Map<String, Object> map = new HashMap<>();
        map.put(NAME, user.getName());
        return map;
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

    /**
     * @return List of the user's followers
     */
    @Override
    public List<Follow> getFollowers() {
        return mFollowers;
    }

    /**
     * Sets the user's followers
     * @param followers List of followers
     */
    public void setFollowers(List<Follow> followers) {
        this.mFollowers = followers;
    }

    /**
     * @return List of users that the user follows
     */
    @Override
    public List<Follow> getFollowing() {
        return mFollowing;
    }

    /**
     * Sets the list of users that the user follows
     * @param following List of users being followed
     */
    public void setFollowing(List<Follow> following) {
        this.mFollowing = following;
    }

    /**
     * @return List of the user's habits
     */
    @Override
    public List<Habit> getHabits() {
        return mHabits;
    }

    /**
     * Set the user's list of habits
     * @param habits List of habits
     */
    public void setHabits(List<Habit> habits) {
        this.mHabits = habits;
    }
}
