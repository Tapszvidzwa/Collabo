package com.example.tapiwa.collabo;

import java.util.HashMap;

/**
 * Created by tapiwa on 8/6/17.
 */

public class NewUser {


    public String name;
    public String uid;
    public String bio;
    public String image_uri;
    public String thumb_image;

    public NewUser() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thum_image) {
        this.thumb_image = thum_image;
    }

    public NewUser(String name, String uid, String bio, String image_uri, String thumb_image) {
        this.name = name;
        this.uid = uid;
        this.bio = bio;
        this.image_uri = image_uri;
        this.thumb_image = thumb_image;
    }



}
