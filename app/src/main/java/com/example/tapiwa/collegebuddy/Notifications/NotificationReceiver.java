package com.example.tapiwa.collegebuddy.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Main.MainFrontPage;
import com.example.tapiwa.collegebuddy.R;

import es.dmoral.toasty.Toasty;
import me.leolin.shortcutbadger.ShortcutBadger;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by tapiwa on 11/8/17.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ShortcutBadger.applyCount(context, 1);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager nM = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(context, MainFrontPage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1,0);
        Notification.Builder mNotify = new Notification.Builder(context);

        Bundle extras = intent.getExtras();
        String assignmentTitle = extras.getString("assignmentTitle");
        int numDaysLeft = extras.getInt("numDaysLeft");

        if(numDaysLeft != 1) {
                mNotify.setContentTitle("Deadline reminder")
                    .setContentText(assignmentTitle + " is due in " + numDaysLeft + " days")
                    .setSmallIcon(R.drawable.ic_newiconcollabo)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.collabo_icon))
                    .setSound(sound);

        } else {
            mNotify.setContentTitle("Deadline reminder")
                    .setContentText(assignmentTitle + " is due TOMORROW")
                    .setSmallIcon(R.drawable.ic_newiconcollabo)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.collabo_icon))
                    .setSound(sound);

        }

        nM.notify(4, mNotify.build());
    }
}
