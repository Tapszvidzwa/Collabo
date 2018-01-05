package com.example.tapiwa.collegebuddy.Authentication;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.example.tapiwa.collegebuddy.Main.HomePage.MainFrontPageActivity;
import com.example.tapiwa.collegebuddy.Miscellaneous.GenericMethods;
import com.example.tapiwa.collegebuddy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase FirebaseDB;
    private DatabaseReference usersBDRef;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAnalytics mFBAnalytics;
    private Boolean isUser;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        isUser = false;

        //FirebaseAnalytics setup
        mFBAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        activity = this;

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


        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(800);

                    if (isUser == true) {
                        confirmUserCredentialsStoredLocally();
                        Intent openMain = new Intent(WelcomeActivity.this, MainFrontPageActivity.class);
                        startActivity(openMain);
                        WelcomeActivity.this.finish();
                    } else {
                        Intent openLoginRegistration = new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(openLoginRegistration);
                        WelcomeActivity.this.finish();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        myThread.start();
    }


    private void confirmUserCredentialsStoredLocally() {


        String username = GenericMethods.getStringFromSharedPreference(activity,
                getString(R.string.user_name));


        if(username.equals("none")) {

            final String user_uid = mAuth.getCurrentUser().getUid().toString();
            final String user_email = mAuth.getCurrentUser().getEmail().toString();
            FirebaseDB = FirebaseDatabase.getInstance();
            usersBDRef = FirebaseDB.getReference(getString(R.string.users));


            usersBDRef.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    NewUser user = dataSnapshot.getValue(NewUser.class);
                    GenericMethods.saveUserCredentialsLocally(
                            activity,
                            user.name,
                            user_email,
                            user_uid
                    );

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}


