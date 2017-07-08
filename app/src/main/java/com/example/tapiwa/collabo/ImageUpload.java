package com.example.tapiwa.collabo;


/**
 * Created by tapiwa on 7/3/17.
 */

public class ImageUpload {


    public ImageUpload(String profileName, String tag, String url) {
        this.profileName = profileName;
        this.tag = tag;
        this.url = url;
    }

    public ImageUpload() {
    }

    public String profileName;
    public String tag;
    public String url;


    public String getProfileName() {
        return profileName;
    }

    public String getTag() {
        return tag;
    }

    public String getUrl() {
        return url;
    }
}
