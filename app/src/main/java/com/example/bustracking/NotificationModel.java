package com.example.bustracking;

public class NotificationModel {
    private String id;
    private String title;
    private String message;
    private long timestamp;

    public NotificationModel() {
        // Needed for Firebase
    }

    public NotificationModel(String id, String title, String message, long timestamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}

