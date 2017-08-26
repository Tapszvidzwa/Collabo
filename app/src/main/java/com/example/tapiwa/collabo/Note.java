package com.example.tapiwa.collabo;

/**
 * Created by tapiwa on 7/6/17.
 */

public class Note {
    private String title;
    private String noteContent;

    public Note(String title, String noteContent) {
        this.title = title;
        this.noteContent = noteContent;
    }

    public String getTitle() {
        return title;
    }



    public String getNoteContent() {
        return noteContent;
    }

}
