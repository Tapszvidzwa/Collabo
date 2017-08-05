package com.example.tapiwa.collabo;

import android.provider.ContactsContract;
import android.widget.ImageView;

/**
 * Created by tapiwa on 8/1/17.
 */

public class BuddieProfiles {


    public String name;
    public String image_uri;
    public String bio;
    public String uid;
    public String thumb_image;

    public BuddieProfiles(String name, String image_uri, String bio, String thumb_image, String uid) {
        this.name = name;
        this.image_uri = image_uri;
        this.bio = bio;
        this.uid = uid;
        this.thumb_image = thumb_image;
    }


    public BuddieProfiles() {
    }

        public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }


}
