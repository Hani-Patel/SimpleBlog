package com.hani.android.simpleblog;

/**
 * Created by SURBHI PATEL on 14-04-2017.
 */

public class Blog {
    private String description;
    private String image;
    private String title;
    private String username;


    public Blog() {
    }

    public Blog(String description, String image, String title) {
        this.description = description;
        this.image = image;
        this.title = title;
        this.username=username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
