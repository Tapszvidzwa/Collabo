package com.example.tapiwa.collabo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;





public class Collabos extends Fragment {


    public Collabos() {
    }


    private ProgressDialog mProgress;
    public GridView gridView;
    public ArrayList<ImageUpload> list;
    public ImageListAdapter adapter;
    private DatabaseReference mDatabaseRef;
    final String FB_DATABASE_PATH = "photos";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View collabos = inflater.inflate(R.layout.collabos, container, false);
        gridView = (GridView) collabos.findViewById(R.id.gridview);
        list = new ArrayList<>();
        adapter = new ImageListAdapter(getContext(), R.layout.images, list);
        gridView.setAdapter(adapter);
        mProgress = new ProgressDialog(getContext());


        mProgress.setMessage("Loading... ");
        mProgress.show();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);


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
                adapter = new ImageListAdapter(getContext(), R.layout.images, list);
                gridView.setAdapter(adapter);
                mProgress.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                ImageUpload item = (ImageUpload) parent.getItemAtPosition(position);

                //Pass the image title and url to DetailsActivity
                Intent intent = new Intent(getContext(), MaximizeImage.class);
                intent.putExtra("title", item.getTag());
                intent.putExtra("image", item.getUrl());
                intent.putExtra("name", item.getProfileName());

                //Start details activity
                startActivity(intent);
            }
        });

        return collabos;
    }

}


