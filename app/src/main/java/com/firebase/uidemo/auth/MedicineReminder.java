package com.firebase.uidemo.auth;

import com.google.firebase.database.Exclude;

public class MedicineReminder {
    int hour;
    int minute;
    String name;

    @Exclude
    String key;

    @Exclude
    int hash;

    public MedicineReminder(){

    }

    public MedicineReminder(int hour, int minute, String name, String key, int hash) {
        this.hour = hour;
        this.minute = minute;
        this.name = name;
        this.key = key;
        this.hash = hash;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getName(){
        return name;
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
