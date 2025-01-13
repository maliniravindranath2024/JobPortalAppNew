package com.application;

public class UserSession {
    private static UserSession instance;
    private int userId;
    private String role;

    // Private constructor to prevent instantiation
    private UserSession() {}

    // Static method to get the single instance of UserSession
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void clearSession() {
        userId = 0;
        role = null;
    }
}
