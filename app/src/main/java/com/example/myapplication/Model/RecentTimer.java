package com.example.myapplication.Model;

public class RecentTimer {
    private final long seconds;
    private final String name;

    public RecentTimer(long seconds, String name) {
        this.seconds = seconds;
        this.name = name;
    }

    public long getSeconds() {
        return seconds;
    }

    public String getName() {
        return name;
    }
}
