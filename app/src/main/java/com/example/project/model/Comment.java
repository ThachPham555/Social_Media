package com.example.project.model;

import java.io.Serializable;

public class Comment implements Serializable {
    private String id;
    private String image;
    private String userId;
    private String username;
    private String commentText;
    private long timestamp;

    public Comment() {
    }

    public Comment(String id, String image, String userId, String username, String commentText, long timestamp) {
        this.id = id;
        this.image = image;
        this.userId = userId;
        this.username = username;
        this.commentText = commentText;
        this.timestamp = timestamp;
    }

    public Comment(String image, String userId, String username, String commentText, long timestamp) {
        this.image = image;
        this.userId = userId;
        this.username = username;
        this.commentText = commentText;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
