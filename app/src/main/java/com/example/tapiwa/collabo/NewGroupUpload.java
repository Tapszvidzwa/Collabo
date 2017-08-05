package com.example.tapiwa.collabo;


/**
 * Created by tapiwa on 7/3/17.
 */

public class NewGroupUpload {

    public String groupName;
    public String groupKey;


    public NewGroupUpload(String groupName, String groupKey) {
        this.groupName = groupName;
        this.groupKey = groupKey;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
        this.groupKey = groupKey;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

}
