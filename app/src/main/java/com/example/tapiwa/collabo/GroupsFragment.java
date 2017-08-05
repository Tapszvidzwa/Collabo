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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


public class GroupsFragment extends Fragment {

    private FloatingActionButton createNewGroupBtn;
    private ListView GroupsList;
    private DatabaseReference mRequestsDatabase;
    private String mCurrent_user_id;
    private View mMainView;


    public GroupsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mMainView = inflater.inflate(R.layout.fragment_groups, container, false);
        GroupsList = (ListView) mMainView.findViewById(R.id.buddie_requests_list);
        mCurrent_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
