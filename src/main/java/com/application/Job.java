package com.application;

public class Job {

    private int jobId;
    private int employerId;
    private String title;
    private String description;
    private String location;
    private String salaryRange;
    private String type;
    private String skillsRequired;
    private String postedAt;
    private String expiryDate;

    public Job() {}

    public Job(int jobId, int employerId, String title, String description, String location, String salaryRange, String type, String skillsRequired, String postedAt, String expiryDate) {
        this.jobId = jobId;
        this.employerId = employerId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.salaryRange = salaryRange;
        this.type = type;
        this.skillsRequired = skillsRequired;
        this.postedAt = postedAt;
        this.expiryDate = expiryDate;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getEmployerId() {
        return employerId;
    }

    public void setEmployerId(int employerId) {
        this.employerId = employerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSkillsRequired() {
        return skillsRequired;
    }

    public void setSkillsRequired(String skillsRequired) {
        this.skillsRequired = skillsRequired;
    }

    public String getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(String postedAt) {
        this.postedAt = postedAt;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
