package com.application;

public class Application {
    private int applicationId;
    private int jobId;
    private int jobSeekerId;
    private String status;

    public Application(int applicationId, int jobId, int jobSeekerId, String status) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.jobSeekerId = jobSeekerId;
        this.status = status;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getJobSeekerId() {
        return jobSeekerId;
    }

    public void setJobSeekerId(int jobSeekerId) {
        this.jobSeekerId = jobSeekerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Application{" +
                "applicationId=" + applicationId +
                ", jobId=" + jobId +
                ", jobSeekerId=" + jobSeekerId +
                ", status='" + status + '\'' +
                '}';
    }
}