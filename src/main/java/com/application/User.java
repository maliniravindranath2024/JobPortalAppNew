package com.application;

public class User {
    private final int userId;
    private final String email;
    private final String role;
    private final String password;

    public User(int userId, String email, String role, String password) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }
}