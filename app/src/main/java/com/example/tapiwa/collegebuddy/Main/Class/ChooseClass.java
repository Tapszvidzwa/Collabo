package com.example.tapiwa.collegebuddy.Main.Class;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tapiwa.collegebuddy.Main.HomePage.MainFrontPageActivity;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.CameraGalleryUploads.CameraGalleryUpload;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by tapiwa on 10/5/17.
 */

public class ChooseClass extends AppCompatActivity {


    public ListView foldersListView;


    public ArrayList<NewClass> list;
    public ClassesAdapter adapter;
    private DatabaseReference mDatabaseRef, mFolderContentsDBRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private Toolbar mToolBar;
    private ProgressDialog mProgress;
    public final static String USER_PRIVATE_LIST_OF_GROUPS = "List_Of_Private_User_Folders";
    private String user;
    private int selectedFolder = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_class);

        initializeViews();
        initializeFirebase();
        grabClassesFromFirebase();
        setOnCLickListeners();


    }

    private void initializeViews() {

        foldersListView = (ListView) findViewById(R.id.folders_to_choose_from_lstV);

        mToolBar = (Toolbar) findViewById(R.id.choose_class_toolbar);
        mToolBar.setTitle("Select a class to put the image");

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        list = new ArrayList<>();
        adapter = new ClassesAdapter(getApplicationContext(), R.layout.privates_folders_item_lst, list);
        foldersListView.setAdapter(adapter);



    }


    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid().toString();

        mDatabaseRef = FirebaseDatabase.getInstance()
                .getReference(USER_PRIVATE_LIST_OF_GROUPS)
                .child(user);
        mDatabaseRef
                .keepSynced(true);
    }

    private void grabClassesFromFirebase() {

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                list.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    NewClass projectNames = Snapshot1.getValue(NewClass.class);
                    list.add(projectNames);
                }
                Collections.reverse(list);
                mDatabaseRef.keepSynced(true);


                adapter = new ClassesAdapter(getApplicationContext(), R.layout.privates_folders_item_lst, list);
                foldersListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void setOnCLickListeners() {

        foldersListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick (AdapterView < ? > parent, View v,int position, long id){

                //upload in the chosen class
                NewClass item = (NewClass) parent.getItemAtPosition(position);
                CameraGalleryUpload firebaseLink = new CameraGalleryUpload(item.getProjectKey());
                firebaseLink.connectFirebaseCloud(getString(R.string.images_fragment));
                CameraGalleryUpload.attemptImageUpload(MainFrontPageActivity.photoFile, MainFrontPageActivity.resultFileUri, getApplicationContext(), getApplication().getString(R.string.images_fragment));
                ChooseClass.this.finish();

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
