package com.example.myapplication;

public class Account {
    private String username;
    private String password;
    private String deviceID;

    public Account(String username, String password, String deviceID) {
        this.username = username;
        this.password = password;
        this.deviceID = deviceID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDeviceID() {
        return deviceID;
    }
}
