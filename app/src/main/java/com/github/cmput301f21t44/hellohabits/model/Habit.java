package com.github.cmput301f21t44.hellohabits.model;

import java.time.Instant;

public interface Habit {
    String getId();
    String getTitle();
    String getReason();
    Instant getDateStarted();

    void setTitle(String title);
    void setReason(String reason);
    void setDateStarted(Instant dateStarted);
}
