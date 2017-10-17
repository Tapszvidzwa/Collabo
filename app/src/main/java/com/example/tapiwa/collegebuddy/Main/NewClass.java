package com.example.tapiwa.collegebuddy.Main;

/**
 * Created by tapiwa on 8/8/17.
 */

public class NewClass {

    public String projectName;
    public String projectKey;
    public String folderColor = "blue";

    public NewClass() {

    }

    public NewClass(String projectName, String projectKey, String folderColor) {
        this.projectName = projectName;
        this.projectKey = projectKey;
        this.folderColor = folderColor;
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
