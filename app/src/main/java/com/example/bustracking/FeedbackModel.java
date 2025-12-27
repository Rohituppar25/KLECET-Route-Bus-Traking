package com.example.bustracking;

public class FeedbackModel {

    private String feedbackId;   // ID of feedback
    private String message;      // feedback message
    private long timestamp;      // stored time
    private String userId;       // logged-in student's UID

    public FeedbackModel() {
        // required for Firebase
    }

    public FeedbackModel(String feedbackId, String message, long timestamp, String userId) {
        this.feedbackId = feedbackId;
        this.message = message;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }
}
