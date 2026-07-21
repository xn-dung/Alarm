package com.example.myapplication.Model;

import java.io.Serializable;

public class Alarm implements Serializable {
    private int id;
    private int hour;
    private int minute;
    private String label;
    private boolean enabled;
    private String repeatDays; // "0,1,2,3,4,5,6" for Mon-Sun
    private String musicUri;
    private int volume;
    private boolean randomMusic;
    private boolean vibrate;
    private boolean loop;
    private int musicId;

    public Alarm() {
        this.enabled = true;
        this.repeatDays = "";
        this.volume = 70;
        this.vibrate = true;
        this.loop = true;
    }

    public int getId() {
        return id;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getVolume() {
        return volume;
    }

    public String getLabel() {
        return label;
    }

    public String getMusicUri() {
        return musicUri;
    }

    public String getRepeatDays() {
        return repeatDays;
    }

    public boolean getEnabled(){return enabled;}
    public boolean getRandomMusic(){return randomMusic;}
    public boolean getLoop(){return loop;}
    public boolean getVibrate(){return vibrate;}

    public int getMusicId() {
        return musicId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setMusicUri(String musicUri) {
        this.musicUri = musicUri;
    }

    public void setRandomMusic(boolean randomMusic) {
        this.randomMusic = randomMusic;
    }

    public void setRepeatDays(String repeatDays) {
        this.repeatDays = repeatDays;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }
}
