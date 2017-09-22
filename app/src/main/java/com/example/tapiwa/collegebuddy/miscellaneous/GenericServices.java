package com.example.tapiwa.collegebuddy.miscellaneous;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.Toast;

import org.joda.time.DateTime;

/**
 * Created by tapiwa on 8/3/17.
 */

public class GenericServices {

    Context context;
    Boolean foregroundStatus = false;

    public GenericServices(Context cxt) {
        this.context = cxt;

    }


    public boolean isConnectingToInternet() {


        int unicode = 0x1F64A;
        String emoji = new String(Character.toChars(unicode));

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        } else {
            Toast toast = Toast.makeText(context, "No internet Connection..." + emoji, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        }
    }


    public static String timeStamp() {

        DateTime dt = new DateTime();
        String time  = dt.toLocalTime().toString().substring(0,5);

        return time;
    }

    public static String date() {
        DateTime dt = new DateTime();
        String year = dt.year().getAsShortText().toString();
        String month = dt.monthOfYear().getAsShortText().toString();
        String day = dt.dayOfMonth().getAsShortText().toString();

        return day + " " + month + ", " + year;
    }


    public boolean isInForeGround() {

        return this.foregroundStatus;

    }


}
