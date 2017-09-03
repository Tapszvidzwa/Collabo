package com.example.tapiwa.collabo;

/**
 * Created by tapiwa on 8/6/17.
 */

public class NewChat {


    public String chatKey;
    public String thumbUri;
    public String name;


    public NewChat() {
    }

    public NewChat(String chatKey, String thumbUri, String name) {
        this.chatKey = chatKey;
        this.thumbUri = thumbUri;
        this.name = name;
    }

    public String getChatKey() {
        return chatKey;
    }
    public String getThumbUri() { return thumbUri;}
    public String getName() { return name;}

}
