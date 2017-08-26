package com.example.tapiwa.collabo;

/**
 * Created by tapiwa on 8/13/17.
 */

public class Message {

    public String message;
    public String time_sent;
    public String sent_from;
    public String uid;

    public Message() {
    }

    public Message(String message, String time_sent, String sent_from, String thumb_uri, String uid) {
        this.message = message;
        this.time_sent = time_sent;
        this.sent_from = sent_from;
        this.thumb_uri = thumb_uri;
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime_sent() {
        return time_sent;
    }

    public void setTime_sent(String time_sent) {
        this.time_sent = time_sent;
    }

    public String getSent_from() {
        return sent_from;
    }

    public void setSent_from(String sent_from) {
        this.sent_from = sent_from;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



    public String getThumb_uri() {
        return thumb_uri;
    }

    public void setThumb_uri(String thumb_uri) {
        this.thumb_uri = thumb_uri;
    }

    public String thumb_uri;

}
