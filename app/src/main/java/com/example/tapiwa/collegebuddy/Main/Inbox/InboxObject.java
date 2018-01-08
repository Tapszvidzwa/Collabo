package com.example.tapiwa.collegebuddy.Main.Inbox;

/**
 * Created by tapiwa on 10/21/17.
 */

public class InboxObject {

    public String senderName;
    public String title;
    public String content;
    public String note_color;
    public String type;
    public String imageUri;
    public String url;
    public String time_sent;
    public String pushKey;

    public InboxObject(String senderName, String title, String content, String noteColor, String type, String pushKey, String time_sent, String imageUri) {
        this.senderName = senderName;
        this.title = title;
        this.time_sent = time_sent;
        this.content = content;
        this.note_color = noteColor;
        this.type = type;
        this.pushKey = pushKey;
        this.imageUri = imageUri;

    }

    public InboxObject(String senderName, String notetitle, String type, String url, String pushKey, String time_sent, String imageUri) {
        this.senderName = senderName;
        this.title = notetitle;
        this.type = type;
        this.url = url;
        this.pushKey = pushKey;
        this.time_sent = time_sent;
        this.imageUri = imageUri;
    }

    public InboxObject() {
    }

    public String getTime_sent() {
        return time_sent;
    }

    public String getType() {
        return type;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getNote_color() {
        return note_color;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }


    public String getUrl() {
        return url;
    }

    public String getPushKey() {
        return pushKey;
    }


}
