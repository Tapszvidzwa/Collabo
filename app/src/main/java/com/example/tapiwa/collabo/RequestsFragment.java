package com.example.tapiwa.collabo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RequestsFragment extends Fragment {

    private FloatingActionButton mSearchForNewBuddies;
    private ListView mRequestsList;
    private DatabaseReference mRequestsDatabase;
    private String mCurrent_user_id;
    private View mMainView;



    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {



        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);
        mRequestsList = (ListView) mMainView.findViewById(R.id.buddie_requests_list);
        mCurrent_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRequestsDatabase = FirebaseDatabase.getInstance().getReference().child(ProfileActivity.BUDDIE_REQUESTS_RECEIVED).child(mCurrent_user_id);


        mSearchForNewBuddies = (FloatingActionButton)  mMainView.findViewById(R.id.search_for_new_buddies);


        mSearchForNewBuddies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent searchBuddies = new Intent(getContext(), SearchBuddiesActivity.class);
                startActivity(searchBuddies);
            }
        });




  return mMainView;
    }



    @Override
    public void onStart() {
        super.onStart();

    }


}
