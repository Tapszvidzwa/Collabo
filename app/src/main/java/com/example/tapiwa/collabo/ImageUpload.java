package com.example.tapiwa.collabo;


/**
 * Created by tapiwa on 7/3/17.
 */

public class ImageUpload {

    private String profileName;
    private String tag;
    private String full_image_uri;
    private String timeUploaded;
    private String chat_room_key;
    private String thumb_uri;
    private String image_key;
    private String uploaderUid;

    public ImageUpload() {
    }


    public ImageUpload(String profileName, String tag, String full_image_uri, String timeUploaded, String chat_room_key, String thumb_uri, String uploaderUid) {
        this.profileName = profileName;
        this.tag = tag;
        this.full_image_uri = full_image_uri;
        this.timeUploaded = timeUploaded;
        this.chat_room_key = chat_room_key;
        this.thumb_uri = thumb_uri;
        this.uploaderUid = uploaderUid;
    }


    public ImageUpload(String tag, String full_image_uri, String thumb_uri, String timeUploaded, String imageKey) {
        this.tag = tag;
        this.full_image_uri = full_image_uri;
        this.timeUploaded = timeUploaded;
        this.thumb_uri = thumb_uri;
        this.image_key = imageKey;
    }


    public void setUploaderUid(String uploaderUid) {
        this.uploaderUid = uploaderUid;
    }


    public String getThumb_uri() {
        return thumb_uri;
    }

    public String getImage_key() {
        return image_key;
    }

    public String getUploaderUid() {
        return uploaderUid;
    }

    public void setImage_key(String image_key) {
        this.image_key = image_key;
    }

    public void setThumb_uri(String thumb_uri) {
        this.thumb_uri = thumb_uri;
    }

    public String getChat_room_key() {
        return chat_room_key;
    }


    public String getTimeUploaded() {
        return timeUploaded;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getTag() {
        return tag;
    }

    public String getFull_image_uri() {
        return full_image_uri;
    }
}
