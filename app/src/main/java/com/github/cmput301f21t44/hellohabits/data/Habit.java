package com.github.cmput301f21t44.hellohabits.data;


import java.time.LocalDate;

public class Habit {
    private String name;
    private String reason;
    private LocalDate dateStarted;

    public Habit(String name, String reason, LocalDate dateStarted) {
        this.name = name;
        this.reason = reason;
        this.dateStarted = dateStarted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(LocalDate dateStarted) {
        this.dateStarted = dateStarted;
    }
}
