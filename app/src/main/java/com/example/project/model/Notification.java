package com.example.project.model;

import java.io.Serializable;


public class Notification implements Serializable {
    private String userId, postId, message, image;

    public Notification() {
    }

    public Notification(String userId, String postId, String message, String image) {
        this.userId = userId;
        this.postId = postId;
        this.message = message;
        this.image = image;
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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
