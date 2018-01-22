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
    public String senderID;
    public String url;
    public String time_sent;
    public String pushKey;

    public InboxObject(String senderName, String title, String content, String noteColor, String type, String pushKey, String time_sent, String senderID) {
        this.senderName = senderName;
        this.title = title;
        this.time_sent = time_sent;
        this.content = content;
        this.note_color = noteColor;
        this.type = type;
        this.pushKey = pushKey;
        this.senderID = senderID;

    }

    public InboxObject(String senderName, String title, String type, String url, String pushKey, String time_sent, String senderID) {
        this.senderName = senderName;
        this.title = title;
        this.type = type;
        this.url = url;
        this.pushKey = pushKey;
        this.time_sent = time_sent;
        this.senderID = senderID;
    }

    public InboxObject() {
    }

    public String getTime_sent() {
        return time_sent;
    }

    public String getType() {
        return type;
    }

    public String getSenderID() {
        return senderID;
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
