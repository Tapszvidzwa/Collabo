package com.example.tapiwa.collegebuddy.Main;

/**
 * Created by tapiwa on 10/6/17.
 */

public class UserSessions {

    private int num_of_sessions = 0;
    private int classContentsCount = 0;

    public UserSessions() {
    }

    public UserSessions(int num) {
        this.num_of_sessions = num;
    }
    public int getClassContentsCount() {
        return classContentsCount;
    }
    public void setClassContentsCount(int classContentsCount) {
        this.classContentsCount = classContentsCount;
    }


    public int getNum_of_sessions() {
        return num_of_sessions;
    }

    public void setNum_of_sessions(int num_of_sessions) {
        this.num_of_sessions = num_of_sessions;
    }

}
