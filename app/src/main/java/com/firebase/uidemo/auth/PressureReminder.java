package com.firebase.uidemo.auth;

import com.google.firebase.database.Exclude;

public class PressureReminder {
    int hour;
    int minute;

    @Exclude
    String key;

    @Exclude
    int hash;

    public PressureReminder(){

    }

    public PressureReminder(int hour, int minute, String key, int hash) {
        this.hour = hour;
        this.minute = minute;
        this.key = key;
        this.hash = hash;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public int getHash(){
        return  hash;
    }
}
