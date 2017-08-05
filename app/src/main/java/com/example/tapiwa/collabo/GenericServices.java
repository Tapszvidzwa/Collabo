package com.example.tapiwa.collabo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by tapiwa on 8/3/17.
 */

public class GenericServices {

    Context context;

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


}
