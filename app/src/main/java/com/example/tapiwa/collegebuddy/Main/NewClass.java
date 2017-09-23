package com.example.tapiwa.collegebuddy.Main;

/**
 * Created by tapiwa on 8/8/17.
 */

public class NewClass {

    public String projectName;
    public String projectKey;

    public NewClass() {

    }

    public NewClass(String projectName, String projectKey) {
        this.projectName = projectName;
        this.projectKey = projectKey;
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
