package com.example.myapplication;

public class Account {

    private String name;
    private String password;

    public Account(String name, String email, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}

