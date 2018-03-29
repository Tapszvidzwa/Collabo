package com.example.tapiwa.collegebuddy.Main.Tasks;

/**
 * Created by tapiwa on 10/21/17.
 */

public class Task {

    private String task;
    private String status;

    public Task() {
    }

    public Task(String task, String status) {
        this.task = task;
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
