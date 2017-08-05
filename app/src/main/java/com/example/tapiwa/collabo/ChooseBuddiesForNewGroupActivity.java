package com.example.tapiwa.collabo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChooseBuddiesForNewGroupActivity extends AppCompatActivity {

    public ListView chooseBuddiesListView;
    public static ArrayList<BuddieProfiles> Firebaselist;
    public ChooseGroupMembersAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersDBRef;
    private DatabaseReference mGroupsDatabaseReference;
    private DatabaseReference mUserListOfGroupsReference;
    public final String GROUPS_DB_PATH = "Groups";
    public final String USER_LIST_OF_GROUPS_PATH = "User_List_Of_Groups";
    private GenericServices internet;
    private ArrayList<BuddieProfiles> newGroupMembers;
    private String mCurrentUserId;
    private FirebaseUser mUser;
    private Button mCreateGroupBtn;
    private Toolbar mToolbar;
    public String mGroupName;
    private ImageView checkBox;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_friends_newgrp);


        chooseBuddiesListView = (ListView) findViewById(R.id.choose_new_group_list);
        mUsersDBRef = FirebaseDatabase.getInstance().getReference().child("Users");
        checkBox = (ImageView) findViewById(R.id.item_checkBox);
        mGroupsDatabaseReference = FirebaseDatabase.getInstance().getReference().child(GROUPS_DB_PATH);
        mUserListOfGroupsReference = FirebaseDatabase.getInstance().getReference().child(USER_LIST_OF_GROUPS_PATH);

        newGroupMembers = new ArrayList<>();
        Firebaselist = new ArrayList<>();
        mGroupName = getIntent().getStringExtra("groupName");
        internet = new GenericServices(getApplicationContext());
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mCreateGroupBtn = (Button) findViewById(R.id.done_create_group_Btn);


        mCurrentUserId = mUser.getUid();
        mToolbar = (Toolbar) findViewById(R.id.choose_grp_members_toolbar);
        mToolbar.setTitle("Choose Group Members");
        mAdapter = new ChooseGroupMembersAdapter(getApplicationContext(), R.layout.create_new_group_item_list, Firebaselist);


       chooseBuddiesListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        chooseBuddiesListView.setAdapter(mAdapter);

        //add the current user to the arraylist group
        mUsersDBRef.child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BuddieProfiles curUserProfile = dataSnapshot.getValue(BuddieProfiles.class);
                newGroupMembers.add(curUserProfile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mDatabaseRef = FirebaseDatabase.getInstance().getReference(ProfileActivity.FRIENDS_DATABASE_PATH).child(mCurrentUserId);
        mDatabaseRef.keepSynced(true);


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch from firebase
                Firebaselist.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    BuddieProfiles profiles = Snapshot1.getValue(BuddieProfiles.class);
                    Firebaselist.add(profiles);
                }

                mAdapter = new ChooseGroupMembersAdapter(getApplicationContext(), R.layout.create_new_group_item_list, Firebaselist);
                chooseBuddiesListView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        mCreateGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (newGroupMembers.size() == 1) {
                    Toast.makeText(getApplicationContext(), "Group needs at least one other person", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    String groupkey = mGroupsDatabaseReference.push().getKey().toString();
                    DatabaseReference ref = mGroupsDatabaseReference.child(groupkey).child(mGroupName);


                    for(int i = 0; i < newGroupMembers.size(); i++) {
                        BuddieProfiles profile = newGroupMembers.get(i);

                        //add user to groups db
                        userUpload grpmember = new userUpload(profile.getUid());
                        ref.child(profile.getUid()).setValue(grpmember);

                        //add group to user's list of groups
                        NewGroupUpload newGroup = new NewGroupUpload(mGroupName, groupkey);
                        mUserListOfGroupsReference.child(profile.getUid()).child(groupkey).setValue(newGroup);
                    }


                    Toast.makeText(getApplicationContext(), "New group ~" + mGroupName + "~ created", Toast.LENGTH_LONG).show();
                    newGroupMembers.clear();
                    finish();

                }
            }
        });

        chooseBuddiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                BuddieProfiles profile = (BuddieProfiles) mAdapter.getItem(position);
                View checkbox = view.findViewById(R.id.item_checkBox);

                if (checkbox.getVisibility() == View.VISIBLE) {
                    checkbox.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "You removed " + profile.getName(), Toast.LENGTH_SHORT).show();
                    newGroupMembers.remove(profile);
                } else {
                    checkbox.setVisibility(View.VISIBLE);
                    newGroupMembers.add(profile);
                    //  view.findViewById(R.id.item_checkBox).setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "You added " + profile.getName(), Toast.LENGTH_SHORT).show();
                    // when checkbox is checked
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }




}




























