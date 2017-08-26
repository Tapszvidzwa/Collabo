package com.example.tapiwa.collabo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by tapiwa on 7/19/17.
 */

public class SortMessages {

    ArrayList<String> storedMessageKeys = new ArrayList<>();

     private Context context;
  //  public SortMessages(Context context) {
    //    this.context = context;
    }


   /* public void addToStoredMessages(String key) {

        if (!storedMessageKeys.contains(key)){
            storedMessageKeys.add(key);
    }

    }

    public void removeFromStoredKeys(String key) {

        if(storedMessageKeys.contains(key)) {
            storedMessageKeys.remove(key);
        }
    }

    public void specialRemove(String key) {
        restoreStoredMessagesPreference();
        removeFromStoredKeys(key);
        savePreferences();
    }
*/


   /* public void savePreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);


        SharedPreferences.Editor storedMessageEditor = sharedPreferences.edit();
        Set<String> set = new HashSet<>();
        set.addAll(storedMessageKeys);
        storedMessageEditor.putStringSet("storedMessages", set);
        storedMessageEditor.commit();

        storedMessageKeys.clear();

    }
*/


  //  public void restoreStoredMessagesPreference() {

   /*     if(Welcome.loggedIn) {
            Welcome.loggedIn = false;
            return;
        }

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        storedMessageKeys.clear();
        Set<String> set = sharedPreferences.getStringSet("storedMessages", null);
        for (String str : set) {
            storedMessageKeys.add(str);
        }

    } */


   /* public ArrayList<ImageUpload> listOfOpenedMessages(ArrayList<ImageUpload> imageUploads){

        ArrayList<ImageUpload> openedMessages = new ArrayList<>();

        for(int i = 0; i < imageUploads.size(); i++) {
            if(storedMessageKeys.contains(imageUploads.get(i).getKey())) {
                openedMessages.add(imageUploads.get(i));
            }

        }

        //// TODO: 7/20/17 fix so that latest opened goes on top of list for opened messages
        return openedMessages;
    } */


