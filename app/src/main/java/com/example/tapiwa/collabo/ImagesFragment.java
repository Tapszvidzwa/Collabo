package com.example.tapiwa.collabo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class ImagesFragment extends Fragment {

    private FloatingActionButton createNewGroupBtn;
    private ListView GroupsList;
    private DatabaseReference mGroupsDatabase;
    private String mCurrent_user_id;
    private GroupsAdapter mAdapter;
    private ArrayList<NewGroupUpload> Firebaselist;
    private View imagesView;


    public ImagesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        imagesView = inflater.inflate(R.layout.images_fragment, container, false);
        View privateImagesFolder = imagesView.findViewById(R.id.private_images_linearlayout);
        View groupImagesFolder = imagesView.findViewById(R.id.group_images_linearLayout);


        privateImagesFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMyImagesActivity = new Intent(getContext(), PrivatesActivity.class);
                startActivity(openMyImagesActivity);
            }
        });

        groupImagesFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGroups = new Intent(getContext(), GroupsActivity.class);
                startActivity(openGroups);
            }
        });


        return imagesView;
    }






}


