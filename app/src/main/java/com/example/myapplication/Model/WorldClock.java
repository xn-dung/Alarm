package com.example.myapplication.Model;

public class WorldClock {
    private final String city;
    private final String time;
    private final String zone;

    public WorldClock(String city, String time, String zone) {
        this.city = city;
        this.time = time;
        this.zone = zone;
    }

    public String getCity() {
        return city;
    }

    public String getTime() {
        return time;
    }

    public String getZone() {
        return zone;
    }
}
