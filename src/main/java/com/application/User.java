package com.application;

public class User {
    protected int userID;
    protected String name;
    protected String location;
    protected String picture;
    protected String about;
    protected Address address;
    protected Contact contact;
    protected String role;
    protected String emailAddress;
    protected String password;

    public User(int userID, String name, String location, String picture, String about, Address address, Contact contact, String role, String emailAddress, String password) {
        this.userID = userID;
        this.name = name;
        this.location = location;
        this.picture = picture;
        this.about = about;
        this.address = address;
        this.contact = contact;
        this.role = role;
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
