package com.example.tapiwa.collabo;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.example.tapiwa.collabo.NotesFragment.adapter;
import static com.example.tapiwa.collabo.NotesFragment.list;
import static com.example.tapiwa.collabo.NotesFragment.listview;
import static com.facebook.FacebookSdk.getApplicationContext;


public class Main extends AppCompatActivity {

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    private FirebaseUser mCurrentUser;
    private String uid;
    private SearchView searchView;
    public static FloatingActionButton actionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        ShortcutBadger.removeCount(Main.this);
        MyFirebaseMessagingService.count = 0;

        actionButton = (FloatingActionButton) findViewById(R.id.fragment_action);

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

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewFolderName();
            }
        });



        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    //privates fragment
                    actionButton.setImageResource(R.drawable.ic_create_new_folder_white_24dp);
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getNewFolderName();
                        }
                    });

                }

                if(position == 1) {
                    //my notes fragment
                    actionButton.setImageResource(R.drawable.ic_note_add_white_24dp);
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent writeNote = new Intent(getApplicationContext(), NewNote.class);
                            startActivity(writeNote);
                        }
                    });

                }

                if(position == 2) {
                    //groups fragment
                    actionButton.setImageResource(R.drawable.ic_group_add_white_24dp);
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getNewGroupName();
                        }
                    });

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);

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
        return super.onCreateOptionsMenu(menu);
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

        if (id == R.id.friends_list) {
            Intent friendList = new Intent(Main.this, BuddiesListActivity.class);
            startActivity(friendList);
        }

        if (id == R.id.requests_lists) {
            Intent requestsList = new Intent(Main.this, RequestsReceivedActivity.class);
            startActivity(requestsList);
        }

        if (id == R.id.search_buddie_name) {
            Intent searchNewFriends = new Intent(Main.this, SearchBuddiesActivity.class);
            startActivity(searchNewFriends);
        }

        if (id == R.id.send_feedback) {
            Intent feedback = new Intent(Main.this, SendFeedBackActivity.class);
            startActivity(feedback);
        }

        return super.onOptionsItemSelected(item);
    }




    private void getNewFolderName() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(("Enter new Course/Project title"));

        int maxLength = 50;
        final EditText givenTitle = new EditText(this);
        givenTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        givenTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        givenTitle.setTextColor(Color.BLACK);
        givenTitle.setVisibility(View.VISIBLE);
        builder.setView(givenTitle);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(givenTitle.getText().toString().length() > 0) {
                  PrivatesFragment.createNewFolder(givenTitle.getText().toString(), PrivatesFragment.USER_PRIVATE_LIST_OF_GROUPS, Main.this);

                } else {
                    Toast.makeText(Main.this, "Please provide a Course/Project name", Toast.LENGTH_SHORT).show();

                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void getNewGroupName() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
        builder.setTitle(("Provide the New Group name"));

        int maxLength = 50;
        final EditText group_name_input = new EditText(Main.this);
        group_name_input.setTextColor(Color.BLACK);
        group_name_input.setVisibility(View.VISIBLE);
        group_name_input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        group_name_input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(group_name_input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //get the group name, send to dialogue to add group members
                addMembersToGroup(group_name_input.getText().toString().trim());


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }


    public void addMembersToGroup(String groupName) {
        Intent listOfFriends = new Intent(Main.this, ChooseBuddiesForNewGroupActivity.class);
        listOfFriends.putExtra("groupName", groupName);
        startActivity(listOfFriends);
    }





    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0 :
                    PrivatesFragment privatesFragment = new PrivatesFragment();
                    return privatesFragment;
                case 1:
                    NotesFragment notesFragment = new NotesFragment();
                    return notesFragment;
                case 2:
                    GroupsFragment groupsFragment = new GroupsFragment();
                    return groupsFragment;
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
                    return "PRIVATES";
                case 1:
                    return "MY NOTES";
                case 2:
                    return "GROUPS";
            }
            return null;
        }
    }

}
