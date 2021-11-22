package com.github.cmput301f21t44.hellohabits.model.habitevent;

import java.time.Instant;

/**
 * Interface to implement habit event functions
 */
public interface HabitEvent {
    /**
     * Get HabitEvent's UUID
     *
     * @return HabitEvent's UUID
     */
    String getId();

    /**
     * Get HabitEvent's parent UUID
     *
     * @return Habit UUID of the HabitEvent's parent
     */
    String getHabitId();

    /**
     * Get Instant that the HabitEvent was denoted
     *
     * @return Instant for when the Habit was completed
     */
    Instant getDate();

    /**
     * Get optional comment
     *
     * @return String comment
     */
    String getComment();

    /**
     * Get optional photo path
     *
     * @return String path to the photo
     */
    String getPhotoPath();

    /**
     * Get optional geolocation
     *
     * @return Location of when the HabitEvent was denoted
     */
    Location getLocation();
}
