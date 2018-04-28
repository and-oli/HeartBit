package com.firebase.uidemo.auth;


import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Record implements Serializable{

    int systolicPressure;
    int diastolicPressure;
    long added;

    @Exclude
    String key;

    public Record(){

    }

    public Record(int systolicPressure, int diastolicPressure, long added, String key) {
        this.systolicPressure = systolicPressure;
        this.diastolicPressure = diastolicPressure;
        this.added = added;
        this.key = key;
    }

    public int getSystolicPressure() {
        return systolicPressure;
    }

    public int getDiastolicPressure() {
        return diastolicPressure;
    }

    public long getAdded() {
        return added;
    }

    @Exclude
    public String getKey() {
        return key;
    }
}
