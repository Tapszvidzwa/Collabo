package com.example.tapiwa.collabo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class GroupsActivity extends AppCompatActivity {

    private FloatingActionButton createNewGroupBtn;
    private ListView GroupsList;
    private DatabaseReference mGroupsDatabase;
    private String mCurrent_user_id;
    private GroupsAdapter mAdapter;
    private Toolbar mToolbar;
    private ArrayList<NewGroupUpload> Firebaselist;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        GroupsList = (ListView) findViewById(R.id.groups_list);
        mToolbar = (Toolbar) findViewById(R.id.groups_activity_toolbar);
        mCurrent_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Firebaselist = new ArrayList<>();
        mAdapter = new GroupsAdapter(GroupsActivity.this, R.layout.groups_item_list, Firebaselist);
        GroupsList.setAdapter(mAdapter);

        mGroupsDatabase = FirebaseDatabase.getInstance().getReference(ChooseBuddiesForNewGroupActivity.USER_LIST_OF_GROUPS_PATH);
        mGroupsDatabase.keepSynced(true);
        mGroupsDatabase.child(mCurrent_user_id).keepSynced(true);


        registerForContextMenu(GroupsList);
        mToolbar.setTitle("Groups");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mGroupsDatabase.child(mCurrent_user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Firebaselist.add(dataSnapshot.getValue(NewGroupUpload.class));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        GroupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NewGroupUpload clickedItem =  (NewGroupUpload) mAdapter.getItem(position);

                Intent openGroup = new Intent(GroupsActivity.this, GroupChatsActivity.class);
                openGroup.putExtra("groupKey", clickedItem.getGroupKey());
                openGroup.putExtra("groupName", clickedItem.getGroupName());
                startActivity(openGroup);

            }
        });

        GroupsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {





                return false;
            }
        });

        createNewGroupBtn = (FloatingActionButton) findViewById(R.id.create_new_group);
        createNewGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewGroupName();
            }
        });
    }


    public void getNewGroupName() {

        AlertDialog.Builder builder = new AlertDialog.Builder(GroupsActivity.this);
        builder.setTitle(("Provide new group name"));
        builder.setIcon(R.drawable.ic_keyboard_black_24dp);

        int maxLength = 50;
        final EditText group_name_input = new EditText(GroupsActivity.this);
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
        Intent listOfFriends = new Intent(GroupsActivity.this, ChooseBuddiesForNewGroupActivity.class);
        listOfFriends.putExtra("groupName", groupName);
        startActivity(listOfFriends);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_fragment_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()){
            case R.id.delete_group:
                continueWithDeleteGroup(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void continueWithDeleteGroup(int position) {


        NewGroupUpload clickedGroup = (NewGroupUpload) mAdapter.getItem(position);
        final String clickedGroupKey= clickedGroup.getGroupKey();
        final String groupName = clickedGroup.getGroupName();

        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(GroupsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(GroupsActivity.this);
        }


        builder.setTitle("Delete Group")
                .setMessage("Are you sure you want to delete this Group? This will delete all the discussions in it.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mGroupsDatabase.child(mCurrent_user_id).child(clickedGroupKey)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {

                                            mAdapter.notifyDataSetChanged();


                                    DatabaseReference ref = FirebaseDatabase
                                                    .getInstance()
                                                    .getReference()
                                                    .child(ChooseBuddiesForNewGroupActivity.GROUPS_DB_PATH);

                                                    ref.child(clickedGroupKey).child(groupName)
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                Toast.makeText(GroupsActivity.this, "Group deleted", Toast.LENGTH_SHORT).show();
                                                                mAdapter.notifyDataSetChanged();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.groups_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Main/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.groups_information) {
            showInformation();
        }


        return super.onOptionsItemSelected(item);
    }


    private void showInformation() {

            AlertDialog.Builder builder = new AlertDialog.Builder(GroupsActivity.this);
            builder.setMessage(R.string.groups_infomation);
            builder.setTitle("Information");
            builder.setIcon(R.drawable.ic_info_black_24dp);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
    }


}


