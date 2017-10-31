package com.example.tapiwa.collegebuddy.Notifications;

import android.os.AsyncTask;

import com.example.tapiwa.collegebuddy.miscellaneous.GenericServices;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by tapiwa on 10/29/17.
 */

public class SendNotification {


    private static String userName;
    private static String uid;


    public SendNotification() {

    }


    public SendNotification(String uid, String userName) {
        this.userName = userName;
        this.uid = uid;
    }



    public static void executeSendNotification() {

       new sendNotificationTask().execute();

    }


    public static class sendNotificationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();

            okhttp3.RequestBody body = new FormBody.Builder()
                    .add("uid", uid)
                    .add("userName", userName)
                    .build();

            Request request = new Request.Builder()
                    .url("http://132.161.242.110/test/sendNotification.php")
                    .post(body)
                    .build();

            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
