package com.example.tapiwa.collegebuddy.Analytics;

import com.example.tapiwa.collegebuddy.Main.HomePage.MainFrontPageActivity;
import com.example.tapiwa.collegebuddy.Miscellaneous.GenericServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by tapiwa on 10/20/17.
 */

public class AppUsageAnalytics {

    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference analyticsRef;
    public static String ANALYTICS = "Analytics";
    private static String user;

    public AppUsageAnalytics() {
    }


    public static void connectFirebaseAnalytics() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        analyticsRef = firebaseDatabase.getReference(ANALYTICS);
        user = MainFrontPageActivity.user;

    }


    public static void incrementPageVisitCount(final String page) {

        connectFirebaseAnalytics();

        analyticsRef.child(user).child(page).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null) {
                    analyticsRef.child(user).child(page).setValue(1);
                } else {
                    Integer old_count = dataSnapshot.getValue(Integer.TYPE);
                    analyticsRef.child(user).child(page).setValue(++old_count);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void recordTime() {
        String new_time = analyticsRef.child(user).child("LOGIN_TIMES").push().getKey();
        analyticsRef.child(user).child("LOGIN_TIMES").child(new_time).setValue(GenericServices.timeStamp());
    }






}
