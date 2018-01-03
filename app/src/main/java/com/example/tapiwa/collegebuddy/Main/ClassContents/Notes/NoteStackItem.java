package com.example.tapiwa.collegebuddy.Main.ClassContents.Notes;

import java.io.Serializable;

/**
 * Created by tapiwa on 10/6/17.
 */

public class NoteStackItem implements Serializable {

    String title;
    String contents;
    String color;

    public NoteStackItem(String title, String contents, String color) {
        this.title = title;
        this.contents = contents;
        this.color = color;
    }


    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public String getColor() {
        return color;
    }


}
