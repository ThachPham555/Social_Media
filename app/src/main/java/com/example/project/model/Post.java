package com.example.project.model;

import java.io.Serializable;

public class Post implements Serializable {
    private String name, des, image, gio, date, userImage;
    private int like;

    public Post() {
    }

    public Post(String name, String des, String image, String gio, String date, String userImage) {
        this.name = name;
        this.des = des;
        this.image = image;
        this.gio = gio;
        this.date = date;
        this.userImage = userImage;
        this.like = 0;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }


    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGio() {
        return gio;
    }

    public void setGio(String gio) {
        this.gio = gio;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
