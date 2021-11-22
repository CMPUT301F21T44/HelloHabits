package com.github.cmput301f21t44.hellohabits.model.social;

import com.github.cmput301f21t44.hellohabits.model.HasKey;

/**
 * Interface for User data
 */
public interface User extends HasKey {
    /**
     * @return The user's email address
     */
    String getEmail();

    /**
     * @return The user's public name
     */
    String getName();

    /**
     * @return Status of follow request (current user - follow -> this user)
     */
    Follow.Status getFollowingStatus();

    /**
     * @return Status of follow request (this user - follow -> current user)
     */
    Follow.Status getFollowerStatus();
}
