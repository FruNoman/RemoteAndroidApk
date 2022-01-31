package com.github.remotesdk.utils;

public class Contact {
    public String id;
    public String name;
    public String mobileNumber;
    public int type;

    public Contact(String id, String name, String mobileNumber, int type) {
        this.id = id;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.type = type;
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

    public int getType() {
        return type;
    }
}
