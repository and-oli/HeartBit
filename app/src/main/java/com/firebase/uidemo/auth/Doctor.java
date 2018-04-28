package com.firebase.uidemo.auth;


public class Doctor {

    String name;
    String phoneNumber;

    public Doctor(){

    }

    public Doctor(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
