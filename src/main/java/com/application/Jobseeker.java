package com.application;

public class Jobseeker {

    private int jobSeekerId;
    private String firstName;
    private String lastName;
    private String phone;
    private String resume;
    private String skills;
    private int experienceYears;
    private String education;

    public Jobseeker() {}

    public Jobseeker(int jobSeekerId, String firstName, String lastName, String phone, String resume, String skills, int experienceYears, String education) {
        this.jobSeekerId = jobSeekerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.resume = resume;
        this.skills = skills;
        this.experienceYears = experienceYears;
        this.education = education;
    }

    public int getJobSeekerId() {
        return jobSeekerId;
    }

    public void setJobSeekerId(int jobSeekerId) {
        this.jobSeekerId = jobSeekerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }
}