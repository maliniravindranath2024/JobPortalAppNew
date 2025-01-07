package com.application;

public class User {
    private int userID;
    private String email;
    private String password;
    private String role;

    // Default Constructor
    public User() {
        this.userID = 0;
        this.email = "";
        this.password = "";
    }

    // Parameterized Constructor
    public User(int userID, String role, String email, String password) {
        this.userID = userID;
        this.role = role;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}