package com.example.tapiwa.collegebuddy.Main.Inbox;

/**
 * Created by tapiwa on 10/21/17.
 */

public class InboxObject {

    private String senderName;
    private String title;
    private String content;
    private String note_color;

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

    public InboxObject(String senderName, String title, String content, String noteColor) {
        this.senderName = senderName;
        this.title = title;
        this.content = content;
        this.note_color = noteColor;
    }

    public InboxObject() {
    }



}
