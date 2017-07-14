package com.example.tapiwa.collabo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.R.id.input;

/**
 * Created by tapiwa on 7/11/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    SharedPreferences sharedPreferences;
    static int count = 0;



   // @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        ShortcutBadger.applyCount(getApplicationContext(), ++count);

        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
     //   Log.d(TAG, "From: " + remoteMessage.getFrom());
      // Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());


        sendNotification(remoteMessage.getData().get("message").toString());
    }


    //Display the notification

    private void sendNotification(String body) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //// TODO: 7/14/17 find a cleaner way of storing the user name, consider firebase?
        String storedUserName = "@" + sharedPreferences.getString("example_text", null);

        String postMessage = body;

        
        //// TODO: 7/14/17 This method only works when the username does not have spaces, fix so that it works on any userName string 
        int i = postMessage.indexOf(' ');
        String userName = postMessage.substring(0, i);

        try {
            if (userName.equals(storedUserName)) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //show new notification badge

        Intent intent = new Intent(this, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Set sound of notifications
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Collabo")
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifiBuilder.build());

    }

}
