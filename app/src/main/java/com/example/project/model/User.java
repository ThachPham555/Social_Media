package com.example.project.model;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String username;
    private String phone;
    private String image;
    private boolean isLikedByCurrentUser;

    public User() {}

    public User(String uid, String username, String phone, String image) {
        this.uid = uid;
        this.username = username;
        this.phone = phone;
        this.image = image;
        this.isLikedByCurrentUser = false;
    }

    public boolean isLikedByCurrentUser() {
        return isLikedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        isLikedByCurrentUser = likedByCurrentUser;
    }

    // Getters and setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
