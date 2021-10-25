package com.github.cmput301f21t44.hellohabits.model;

import java.time.Instant;

public interface HabitEventRepository<T extends HabitEvent> {
    void insert(String habitId, String comment);

    void delete(T habitEvent);

    T update(String id, Instant date, String comment, String photoPath, Location location);
}
