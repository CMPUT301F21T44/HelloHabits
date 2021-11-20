package com.github.cmput301f21t44.hellohabits.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interface for User data
 */
public interface User {
    /**
     * @return The user's email address
     */
    String getEmail();

    /**
     * @return The user's public name
     */
    String getName();

    /**
     * @return List of the user's followers
     */
    List<Follow> getFollowers();

    /**
     * @return List of users that the user follows
     */
    List<Follow> getFollowing();

    /**
     * @return List of the user's habits
     */
    List<Habit> getHabits();

    interface Follow {
        String getEmail();

        Status getStatus();


        /**
         * Enum for describing the follow relationship between users
         */
        enum Status {
            /**
             * User has requested to follow
             */
            REQUESTED("requested"),
            /**
             * User's follow request has been accepted
             */
            ACCEPTED("accepted");

            /**
             * Constant map to easily convert between enum and text
             * https://stackoverflow.com/questions/604424/how-to-get-an-enum-value-from-a-string-value-in-java
             */
            private static final Map<String, Status> ENUM_MAP;

            static {
                Map<String, Status> map = new ConcurrentHashMap<>();
                for (Status instance : Status.values()) {
                    map.put(instance.getText().toLowerCase(), instance);
                }
                ENUM_MAP = Collections.unmodifiableMap(map);
            }

            private final String text;

            Status(String text) {
                this.text = text;
            }

            public static Status get(String text) {
                return ENUM_MAP.get(text.toLowerCase());
            }

            public String getText() {
                return text;
            }
        }
    }
}
