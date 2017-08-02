package com.example.tapiwa.collabo;

import android.app.Notification;
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

    public static  int NOTIFICATION_ID = 1;
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

        String receivedMsg = remoteMessage.getData().get("message").toString();

        String parts[] = receivedMsg.split("/");

        String username = parts[0];
        String messagetype = parts[1];
        String messageBody = parts[2];
        String messagekey = parts[3];
        

        sendNotification(username, messagetype,messageBody, messagekey);
    }


    //Display the notification

    private void sendNotification(String username, String messagetype, String messageBody, String messagekey) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //// TODO: 7/14/17 find a cleaner way of storing the user name, consider firebase?
        String storedUserName = "@" + sharedPreferences.getString("example_text", null);

        try {
            if (username.equals(storedUserName)) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


     //Send notification to Tags
        SortMessages sortMessages = new SortMessages(getApplicationContext());
        if(Tags.isInForeGround) {
            //// TODO: 7/21/17 find a way to update the Tags UI fragment if it is in the foreground
            // sortMessages.removeFromStoredKeys(messagekey);
        } else {
            if(Tags.onStopCalled) {
                sortMessages.restoreStoredMessagesPreference();
                sortMessages.removeFromStoredKeys(messagekey);
                sortMessages.savePreferences();
            }
        }
        
        //show new notification badge on icon
        Intent intent = new Intent(this, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Set sound of notifications
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Collabo:" + messagetype)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notifiBuilder.build());

    }

}
