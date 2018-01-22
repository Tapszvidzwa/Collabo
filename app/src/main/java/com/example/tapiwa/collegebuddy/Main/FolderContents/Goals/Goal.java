package com.example.tapiwa.collegebuddy.Main.FolderContents.Goals;

/**
 * Created by tapiwa on 10/21/17.
 */

public class Goal {

    private String goal;
    private String completion;
    private String pushkey;

    public Goal() {
    }

    public Goal(String goal, String completion, String goalKey) {
        this.goal = goal;
        this.completion = completion;
        this.pushkey = goalKey;
    }

    public String getCompletion() {
        return completion;
    }

    public String getGoal() {
        return goal;
    }

    public String getPushkey() {
        return pushkey;
    }

}
