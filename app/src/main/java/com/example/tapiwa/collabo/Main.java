package com.example.tapiwa.collabo;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import me.leolin.shortcutbadger.ShortcutBadger;


public class Main extends AppCompatActivity {

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    private FirebaseUser mCurrentUser;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        ShortcutBadger.removeCount(Main.this);
        MyFirebaseMessagingService.count = 0;

        //Connect to Facebook analytics
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //Subscribe to topic and get token from firebase
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();


        // Create the mAdapter that will return a fragment for each of the three
        // primary sections of the activity.

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections unopenedMessagesadapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mViewPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



    }

    @Override
    public void onResume(){
        super.onResume();
        CancelNotification(this, MyFirebaseMessagingService.NOTIFICATION_ID);
    }

    public void CancelNotification(Context ctx, int notifyId) {
        String notificationService = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx
                .getSystemService(notificationService);
        nMgr.cancel(notifyId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Main/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        //// TODO: 8/1/17 Change these settings to custom settings
        if (id == R.id.action_settings) {
            Intent settings = new Intent(Main.this, Settings.class);
            startActivity(settings);
        }

        if (id == R.id.action_profile) {
            Intent userProfile = new Intent(Main.this, ProfileActivity.class);
            String uid = mCurrentUser.getUid();
            userProfile.putExtra("uid", uid);
            userProfile.putExtra("myProfile", "mine");
            startActivity(userProfile);
        }

        if(id == R.id.friends_list) {
            Intent friendList = new Intent(Main.this, BuddiesListActivity.class);
            startActivity(friendList);
        }

        return super.onOptionsItemSelected(item);
    }



    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0 :
                    Privates privates = new Privates();
                    return privates;
                case 1:
                    GroupsFragment groupsFragment = new GroupsFragment();
                    return groupsFragment;
                case 2:
                    RequestsReceivedFragment requestsReceivedFragment = new RequestsReceivedFragment();
                    return requestsReceivedFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PRIVATE";
                case 1:
                    return "GROUPS";
                case 2:
                    return "REQUESTS";
            }
            return null;
        }
    }

}
