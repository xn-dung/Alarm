package com.example.myapplication.Model;

public class SavedMusic {
    private final int id;
    private final String name;
    private final String uri;

    public SavedMusic(int id, String name, String uri) {
        this.id = id;
        this.name = name;
        this.uri = uri;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }
}
