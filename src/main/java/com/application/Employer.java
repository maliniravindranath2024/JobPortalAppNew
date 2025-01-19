package com.application;

public class Employer {

    private int employerId;
    private String companyName;
    private String companyWebsite;
    private String contactPerson;
    private String phone;
    private String companyDescription;

    public Employer() {}

    public Employer(int employerId, String companyName, String companyWebsite, String contactPerson, String phone, String companyDescription) {
        this.employerId = employerId;
        this.companyName = companyName;
        this.companyWebsite = companyWebsite;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.companyDescription = companyDescription;
    }

    public int getEmployerId() {
        return employerId;
    }

    public void setEmployerId(int employerId) {
        this.employerId = employerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }
}