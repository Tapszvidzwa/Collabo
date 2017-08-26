package com.example.tapiwa.collabo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RequestsReceivedActivity extends AppCompatActivity {

    public ListView requestsListview;
    public static ArrayList<BuddieProfiles> Firebaselist;
    public BuddieProfilesAdapter adapter;
    private DatabaseReference mDatabaseRef;
    private String mCurrentUserId;
    private FirebaseUser mUser;
    private FloatingActionButton mSearchForNewBuddies;
    private Toolbar mToolbar;
    final String FB_DATABASE_PATH = ProfileActivity.BUDDIE_REQUESTS_RECEIVED;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        requestsListview = (ListView) findViewById(R.id.buddie_requests_list);
        mToolbar = (Toolbar) findViewById(R.id.requests_toolbar);
        mToolbar.setTitle("New Buddie Requests");
        setSupportActionBar(mToolbar);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Firebaselist = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserId = mUser.getUid();
        adapter = new BuddieProfilesAdapter(getApplicationContext(), R.layout.buddie_request_item_list, Firebaselist);
        requestsListview.setAdapter(adapter);


        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        mDatabaseRef.keepSynced(true);


        mDatabaseRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                Firebaselist.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    BuddieProfiles profiles = Snapshot1.getValue(BuddieProfiles.class);
                    Firebaselist.add(profiles);
                }

                adapter = new BuddieProfilesAdapter(getApplicationContext(), R.layout.buddie_request_item_list, Firebaselist);
                requestsListview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        requestsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Get item at position
                BuddieProfiles profile = (BuddieProfiles) parent.getItemAtPosition(position);

                Intent openRequestProfile = new Intent(getApplicationContext(), ProfileActivity.class);
                openRequestProfile.putExtra("intent", "request");
                openRequestProfile.putExtra("uid", profile.getUid());
                openRequestProfile.putExtra("myProfile", "notMine");

                startActivity(openRequestProfile);

            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }





}
