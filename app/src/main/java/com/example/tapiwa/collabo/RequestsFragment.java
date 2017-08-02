package com.example.tapiwa.collabo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class RequestsFragment extends Fragment {

    private FloatingActionButton mSearchForNewBuddies;


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
        // Inflate the layout for this fragment


        View requests = inflater.inflate(R.layout.fragment_requests, container, false);

        mSearchForNewBuddies = (FloatingActionButton) requests.findViewById(R.id.search_for_new_buddies);


        mSearchForNewBuddies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent searchBuddies = new Intent(getContext(), SearchBuddiesActivity.class);
                startActivity(searchBuddies);
            }
        });

  return requests;
    }

}
