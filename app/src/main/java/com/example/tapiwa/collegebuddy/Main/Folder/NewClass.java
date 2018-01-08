package com.example.tapiwa.collegebuddy.Main.Folder;

/**
 * Created by tapiwa on 8/8/17.
 */

public class NewClass {

    public String projectName;
    public String projectKey;
    public String folderColor = "blue";
    public int num_folders = 0;
    public int num_docs = 0;
    public int num_notes = 0;
    public int num_images = 0;

    public NewClass() {

    }

    public NewClass(String projectName, String projectKey, String folderColor) {
        this.projectName = projectName;
        this.projectKey = projectKey;
        this.folderColor = folderColor;
    }

    public int getNum_folders() {
        return num_folders;
    }

    public int getNum_docs() {
        return num_docs;
    }

    public int getNum_notes() {
        return num_notes;
    }

    public int getNum_images() {
        return num_images;
    }

    public String getFolderColor() {
        return folderColor;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }
}
