package com.example.microprojectmad;

public class Notification {
    private String id;
    private String title;
    private String message;
    private String type;
    private String itemId;
    private String senderId;
    private String timestamp;

    public Notification(String title, String message, String itemId) {
        this.title = title;
        this.message = message;
        this.itemId = itemId;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public String getItemId() { return itemId; }
    public String getSenderId() { return senderId; }
    public String getTimestamp() { return timestamp; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setType(String type) { this.type = type; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}