package com.github.remotesdk.utils;

public class CallHistory {
    private String name;
    private String number;
    private int type;
    private long callTime;

    public CallHistory(String name, String number, int type, long callTime)  {
        this.name = name;
        this.number = number;
        this.type = type;
        this.callTime = callTime;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public int getType() {
        return type;
    }

    public long getCallTime() {
        return callTime;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
