package com.example.tapiwa.collabo;


/**
 * Created by tapiwa on 7/3/17.
 */

public class ImageUpload {

    public String profileName;
    public String tag;
    public String url;
    public String timeUploaded;
    public String chatRoom;
    public int unreadFlag;


    public ImageUpload(String profileName, String tag, String url, String timeUploaded, String chatRoom, int unreadFlag) {
        this.profileName = profileName;
        this.tag = tag;
        this.url = url;
        this.timeUploaded = timeUploaded;
        this.chatRoom = chatRoom;
    }


    public ImageUpload(String tag, String url, String timeUploaded) {
        this.tag = tag;
        this.url = url;
        this.timeUploaded = timeUploaded;
    }

    public ImageUpload() {
    }


    public String getTimeUploaded() {
        return timeUploaded;
    }

    public String getChatRoom() {
        return chatRoom;
    }


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
