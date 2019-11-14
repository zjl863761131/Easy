package com.example.easy.ui.cloud;

import android.widget.ImageView;

import java.util.Date;

public class CloudPhoto {
    public String username = null;
    public String filename = null;
    public String filepath = null;
    public String uploadtime = null;
    public String age = null;
    public String score = null;
    public String path = null;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setUploadtime(String uploadtime) {
        this.uploadtime = uploadtime;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setScore(String score) {
        this.score = score;
    }


    public String getUsername() {
        return username;
    }

    public String getFilename() {
        return filename;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getUploadtime() {
        return uploadtime;
    }

    public String getAge() {
        return age;
    }

    public String getScore() {
        return score;
    }
}
