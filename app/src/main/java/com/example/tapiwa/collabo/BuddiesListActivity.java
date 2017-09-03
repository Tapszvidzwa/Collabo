package com.example.tapiwa.collabo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class BuddiesListActivity extends AppCompatActivity {

    public ListView BuddiesListView;
    public static ArrayList<BuddieProfiles> Firebaselist;
    public BuddieFriendsAdapter adapter;
    private DatabaseReference mDatabaseRef;
    private String mCurrentUserId;
    private FirebaseUser mUser;
    private String currentUserThumbUri;
    private Toolbar mToolbar;

    public static final String DIRECT_CHAT_FIREBASE_REFERENCE = "Direct_Chat_List";
    private final String DIRECT_CHAT_MESSAGES = "Direct_Chat_Messages";
    private FirebaseDatabase firebaseDatabase;
    public static  DatabaseReference personalChatsDbRef;
    private String thisUserName, otherUserName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddies_list);


        BuddiesListView = (ListView) findViewById(R.id.buddies_listview);
        Firebaselist = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mCurrentUserId = mUser.getUid();
        mToolbar = (Toolbar) findViewById(R.id.buddies_list_toolbar);
        mToolbar.setTitle("Your Buddies");
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        adapter = new BuddieFriendsAdapter(getApplicationContext(), R.layout.buddies_item_list, Firebaselist);

        BuddiesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        BuddiesListView.setAdapter(adapter);


        firebaseDatabase = FirebaseDatabase.getInstance();
        personalChatsDbRef = firebaseDatabase.getReference(DIRECT_CHAT_FIREBASE_REFERENCE);




        mDatabaseRef = FirebaseDatabase.getInstance().getReference(ProfileActivity.FRIENDS_DATABASE_PATH);
        mDatabaseRef.keepSynced(true);


        mDatabaseRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch from firebase
                Firebaselist.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    BuddieProfiles profiles = Snapshot1.getValue(BuddieProfiles.class);
                    Firebaselist.add(profiles);
                }

                adapter = new BuddieFriendsAdapter(getApplicationContext(), R.layout.buddies_item_list, Firebaselist);
                BuddiesListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");

        databaseReference.child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NewUser newUser = dataSnapshot.getValue(NewUser.class);
                currentUserThumbUri = newUser.image_uri;
                thisUserName = newUser.name;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        BuddiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                BuddieProfiles profile = Firebaselist.get(position);
                String chatKey = profile.getUid();
                String thumbUri = profile.getThumb_image();

                NewChat newChat = new NewChat(chatKey, thumbUri, profile.getName());
                NewChat newChatForOtherUser = new NewChat(mCurrentUserId, currentUserThumbUri, thisUserName);

                personalChatsDbRef.child(mCurrentUserId).child(chatKey).setValue(newChat);
                personalChatsDbRef.child(chatKey).child(mCurrentUserId).setValue(newChatForOtherUser);

                Intent openChat = new Intent(BuddiesListActivity.this, OneToOneChats.class);
                openChat.putExtra("chat_room_key", chatKey);
                startActivity(openChat);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        finish();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.buddies_menu_activity, menu);
        MenuItem searchItem = menu.findItem(R.id.search_buddie);

        SearchView searchView;
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchImage(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchImage(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Main/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.buddies_information) {
            showInformation();
        }


        return super.onOptionsItemSelected(item);
    }


    private void searchImage(String imageLabel) {

        Query searchImage = mDatabaseRef.orderByChild("name")
                .startAt(imageLabel)
                .endAt(imageLabel + "\uf8ff");


        searchImage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                Firebaselist.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    BuddieProfiles profile = Snapshot1.getValue(BuddieProfiles.class);
                    Firebaselist.add(profile);
                }


                adapter = new BuddieFriendsAdapter(getApplicationContext(), R.layout.buddies_item_list, Firebaselist);
                BuddiesListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }




    private void showInformation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(BuddiesListActivity.this);
        builder.setTitle("Information");
        builder.setMessage(R.string.buddies_activity_information);
        builder.setIcon(R.drawable.ic_info_black_24dp);

        builder.setPositiveButton("Find new Buddies", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent openSearchBuddies = new Intent(BuddiesListActivity.this, SearchBuddiesActivity.class);
                startActivity(openSearchBuddies);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}




























