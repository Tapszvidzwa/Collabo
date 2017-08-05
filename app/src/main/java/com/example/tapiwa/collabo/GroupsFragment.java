package com.example.tapiwa.collabo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GroupsFragment extends Fragment {

    private FloatingActionButton createNewGroupBtn;
    private ListView GroupsList;
    private ArrayList<String> GroupNamesList;
    private DatabaseReference mGroupsDatabase;
    private String mCurrent_user_id;
    private GroupsAdapter mAdapter;
    private ArrayList<NewGroupUpload> Firebaselist;
    private View mMainView;



    public GroupsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mMainView = inflater.inflate(R.layout.fragment_groups, container, false);
        GroupsList = (ListView) mMainView.findViewById(R.id.groups_list);
        GroupNamesList = new ArrayList<>();
        mCurrent_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Firebaselist = new ArrayList<>();
        mAdapter = new GroupsAdapter(getContext(), R.layout.groups_item_list, Firebaselist);

        mGroupsDatabase = FirebaseDatabase.getInstance().getReference(ChooseBuddiesForNewGroupActivity.USER_LIST_OF_GROUPS_PATH);
        mGroupsDatabase.keepSynced(true);


        mGroupsDatabase.child(mCurrent_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {


                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        GroupNamesList.add(dataSnapshot1.getKey());
                    }

                //CONSIDER USING A QUERY INSTEAD HERE
                    for(int i = 0; i < GroupNamesList.size(); i++) {

                        mGroupsDatabase.child(mCurrent_user_id).child(GroupNamesList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Firebaselist.add(dataSnapshot.getValue(NewGroupUpload.class));
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    GroupsList.setAdapter(mAdapter);



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        mAdapter = new GroupsAdapter(getContext(), R.layout.groups_item_list, Firebaselist);
        GroupsList.setAdapter(mAdapter);


/*
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

        */


        createNewGroupBtn = (FloatingActionButton)  mMainView.findViewById(R.id.create_new_group);


        createNewGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           getNewGroupName();

            }
        });

        return mMainView;
    }


    public void getNewGroupName() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(("Provide the New Group name"));

        int maxLength = 50;
        final EditText groupName = new EditText(getContext());
        groupName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        groupName.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(groupName);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //get the group name, send to dialogue to add group members
                addMembersToGroup(groupName.getText().toString().trim());


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

        Intent listOfFriends = new Intent(getContext(), ChooseBuddiesForNewGroupActivity.class);
        listOfFriends.putExtra("groupName", groupName);
        startActivity(listOfFriends);

    }


}


