package com.example.tapiwa.collabo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class Tags extends Fragment {

    public Tags() {
    }

    public ListView listview;
    public ArrayList<ImageUpload> list;
    public TagListAdapter adapter;
    private DatabaseReference mDatabaseRef;
    final String FB_DATABASE_PATH = "photos";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View tags = inflater.inflate(R.layout.tags, container, false);
        listview = (ListView) tags.findViewById(R.id.listView);
        list = new ArrayList<>();
        adapter = new TagListAdapter(getContext(), R.layout.tag_item_list, list);
        listview.setAdapter(adapter);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        mDatabaseRef.keepSynced(true);


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                list.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    ImageUpload img = Snapshot1.getValue(ImageUpload.class);
                    list.add(img);
                }
                Collections.reverse(list);


                //init adapter
                adapter = new TagListAdapter(getContext(), R.layout.tag_item_list, list);
                listview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                ImageUpload item = (ImageUpload) parent.getItemAtPosition(position);

                //Pass the image title and url to DetailsActivity
                Intent intent = new Intent(getContext(), MaximizeImage.class);
                intent.putExtra("title", item.getTag());
                intent.putExtra("image", item.getUrl());
                intent.putExtra("name", item.getProfileName());
                intent.putExtra("time", item.getTimeUploaded());

                //Start details activity
                startActivity(intent);
            }
        });

        return tags;
    }
}