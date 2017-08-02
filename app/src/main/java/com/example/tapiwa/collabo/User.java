package com.example.tapiwa.collabo;

/**
 * Created by tapiwa on 7/27/17.
 */

public class User {

    private String name;
    private  String uid;

    public User(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
