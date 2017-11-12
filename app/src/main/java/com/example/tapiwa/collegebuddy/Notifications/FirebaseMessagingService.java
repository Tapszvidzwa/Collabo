package com.example.tapiwa.collegebuddy.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.tapiwa.collegebuddy.Main.Inbox.InboxFragment;
import com.example.tapiwa.collegebuddy.R;
import com.google.firebase.messaging.RemoteMessage;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by tapiwa on 10/29/17.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        showNotification(remoteMessage.getData().get("message"));
    }


    private void showNotification(String message) {

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        ShortcutBadger.applyCount(this, 1);

        Intent i = new Intent(this, InboxFragment.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setSound(sound)
                .setSmallIcon(R.drawable.ic_newiconcollabo)
                .setContentTitle("You have a new message")
                .setContentText(message)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.collabo_icon))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());


    }
}
