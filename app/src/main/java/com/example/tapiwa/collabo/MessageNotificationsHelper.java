package com.example.tapiwa.collabo;

import java.util.ArrayList;


/**
 * Created by tapiwa on 7/19/17.
 */

public class MessageNotificationsHelper {

    public static ArrayList<Integer> list = new ArrayList<>();

    public MessageNotificationsHelper() {
    }


    public ArrayList<Integer> opened() {
        return list;
    }

    public void addOpened(Integer position) {
        if (!list.contains(position)) {
            list.add(position);
        }
    }

    public void restructureAdded() {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, list.get(i) + 1);
        }
            return;
    }

    public void restructureRemoved(int startingPosition) {

        if (startingPosition == 0) {
            for (int i = 0; i < list.size(); i++) {
                list.set(i, list.get(i) - 1);
            }
            return;
        } else {
            list.remove(startingPosition);
            for (int i = startingPosition; i < list.size(); i++) {
                list.set(i, list.get(i) - 1);
            }
        }
        return;
    }

    public void clearNotitifications() {
        list.clear();
    }

}
