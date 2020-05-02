package com.example.iot;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String username;
    private String imageURL;
    private String name;
    private String lastname;

    public User(String id, String username, String imageURL, String name, String lastname) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.name = name;
        this.lastname = lastname;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
