package com.example.tapiwa.collegebuddy.Main.FolderContents.Assignments;

/**
 * Created by tapiwa on 9/26/17.
 */

public class Assignment {


    public Assignment () {

    }

    public Assignment(String title, String dueDate) {
        this.title = title;
        this.dueDate = dueDate;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    private String dueDate;

}
