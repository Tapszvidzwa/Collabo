package com.example.tapiwa.collabo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class RequestsReceivedFragment extends Fragment {

    public RequestsReceivedFragment() {
    }

    public ListView requestsListview;
    public static ArrayList<BuddieProfiles> Firebaselist;
    public BuddieProfilesAdapter adapter;
    private DatabaseReference mDatabaseRef;
    private String mCurrentUserId;
    private FirebaseUser mUser;
    private FloatingActionButton mSearchForNewBuddies;
    final String FB_DATABASE_PATH = ProfileActivity.BUDDIE_REQUESTS_RECEIVED;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View requests = inflater.inflate(R.layout.fragment_requests, container, false);
        requestsListview = (ListView) requests.findViewById(R.id.buddie_requests_list);
        Firebaselist = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserId = mUser.getUid();
        adapter = new BuddieProfilesAdapter(getContext(), R.layout.buddie_request_item_list, Firebaselist);
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

                adapter = new BuddieProfilesAdapter(getContext(), R.layout.buddie_request_item_list, Firebaselist);
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

                Intent openRequestProfile = new Intent(getContext(), ProfileActivity.class);
                openRequestProfile.putExtra("intent", "request");
                openRequestProfile.putExtra("uid", profile.getUid());
                openRequestProfile.putExtra("myProfile", "notMine");

                startActivity(openRequestProfile);

            }
        });



        mSearchForNewBuddies = (FloatingActionButton)  requests.findViewById(R.id.search_for_new_buddies);


        mSearchForNewBuddies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent searchBuddies = new Intent(getContext(), SearchBuddiesActivity.class);
                startActivity(searchBuddies);
            }
        });


        return requests;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
