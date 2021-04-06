package com.github.remotesdk.utils;

public class Contact {
    public String id;
    public String name;
    public String mobileNumber;

    public Contact(String id, String name, String mobileNumber) {
        this.id = id;
        this.name = name;
        this.mobileNumber = mobileNumber;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}
