package com.application;

public class Interview {

        private int interviewId;
        private int applicationId;
        private int employerId;
        private int jobSeekerId;
        private String scheduledDate;
        private String interviewType;
        private String status;
        private String feedback;

        // Default constructor
        public Interview() {}

        // Parameterized constructor
        public Interview(int interviewId, int applicationId, int employerId, int jobSeekerId, String scheduledDate,
                       String interviewType, String status, String feedback) {
            this.interviewId = interviewId;
            this.applicationId = applicationId;
            this.employerId = employerId;
            this.jobSeekerId = jobSeekerId;
            this.scheduledDate = scheduledDate;
            this.interviewType = interviewType;
            this.status = status;
            this.feedback = feedback;
        }

        // Getters and Setters
        public int getInterviewId() {
            return interviewId;
        }

        public void setInterviewId(int interviewId) {
            this.interviewId = interviewId;
        }

        public int getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(int applicationId) {
            this.applicationId = applicationId;
        }

        public int getEmployerId() {
            return employerId;
        }

        public void setEmployerId(int employerId) {
            this.employerId = employerId;
        }

        public int getJobSeekerId() {
            return jobSeekerId;
        }

        public void setJobSeekerId(int jobSeekerId) {
            this.jobSeekerId = jobSeekerId;
        }

        public String getScheduledDate() {
            return scheduledDate;
        }

        public void setScheduledDate(String scheduledDate) {
            this.scheduledDate = scheduledDate;
        }

        public String getInterviewType() {
            return interviewType;
        }

        public void setInterviewType(String interviewType) {
            this.interviewType = interviewType;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }
}
