package com.example.tapiwa.collabo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.analytics.FirebaseAnalytics;



public class Welcome extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAnalytics mFBAnalytics;
    private Boolean isUser;
    public static Boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        isUser = false;

        //FirebaseAnalytics setup
      /*  mFBAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    isUser = true;
                } else {
                    // User is signed out
                }
            }
        };
 */
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1300);
                    Intent openLogin = new Intent(Welcome.this, Login.class);
                    startActivity(openLogin);
                    Welcome.this.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        myThread.start();
    }

}


