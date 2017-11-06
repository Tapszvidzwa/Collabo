package com.example.tapiwa.collegebuddy.Main.Inbox;

/**
 * Created by tapiwa on 10/21/17.
 */

public class InboxObject {

    private String senderName;
    private String title;
    private String content;
    private String note_color;
    private String type;
    private String url;


    private String pushKey;

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

    public InboxObject(String senderName, String title, String content, String noteColor, String type, String pushKey) {
        this.senderName = senderName;
        this.title = title;
        this.content = content;
        this.note_color = noteColor;
        this.type = type;
        this.pushKey = pushKey;
    }

    public InboxObject(String senderName, String title, String type, String url, String pushKey) {
        this.senderName = senderName;
        this.title = title;
        this.type = type;
        this.url = url;
        this.pushKey = pushKey;
    }

    public InboxObject() {
    }


    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getPushKey() {
        return pushKey;
    }


}
