package com.example.tapiwa.collegebuddy.Main.ClassContents.StackImages;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.StackView;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.Main.ClassContents.ClassContentsMain.ClassContentsMainActivity;
import com.example.tapiwa.collegebuddy.Main.ClassContents.Images.MaximizePrivateImageActivity;
import com.example.tapiwa.collegebuddy.CameraGalleryUploads.NewImage;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class StackCardsImages extends AppCompatActivity {

    private FirebaseUser mCurrentUser;
    public static String className, projectKey;
    private FirebaseAuth mAuth;
    public static DatabaseReference mPrivateFullImageDatabaseRef;
    public ArrayList<NewImage> listImages;
    private StackViewAdapter stackViewAdapter;
    private StackView stackView;
    private Toolbar mToolbar;


    public static final String PRIVATE_FOLDERS_CONTENTS = "Private_Folders_Contents";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stack_card_images);


        initializeViews();
        firebaseInitialization();
        initializeListeners();

        //Connect to Facebook analytics
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

    }

    private void initializeViews() {

        listImages = new ArrayList<>();
        stackView = (StackView) findViewById(R.id.swipeStack);
        mToolbar = (Toolbar) findViewById(R.id.swipeviewToolbar);
        mToolbar.setTitle("Images");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //swipeStackAdapter = new SwipeStackAdapter(listCards, StackCardsImages.this);
       // stackView.setAdapter(swipeStackAdapter);

    }

    private void initializeListeners() {

        mPrivateFullImageDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listImages.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    NewImage privateImage = Snapshot1.getValue(NewImage.class);
                    listImages.add(privateImage);
                }
                Collections.reverse(listImages);
                stackViewAdapter = new StackViewAdapter(listImages, R.layout.stack_image_item, StackCardsImages.this);
                stackView.setAdapter(stackViewAdapter);
                //  swipeStackAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        stackView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewImage imageUpload = (NewImage) stackViewAdapter.getItem(position);

                Intent maximizeImage = new Intent(StackCardsImages.this, MaximizePrivateImageActivity.class);
                maximizeImage.putExtra("imageUri", imageUpload.getFull_image_uri());
                maximizeImage.putExtra("tag", imageUpload.getTag());
                maximizeImage.putExtra("thumbUri", imageUpload.getThumb_uri());
                maximizeImage.putExtra("imageKey", imageUpload.getImage_key());
                startActivity(maximizeImage);
            }
        });

    }

    private void firebaseInitialization() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        String user = mAuth.getCurrentUser().getUid().toString();

        mPrivateFullImageDatabaseRef = FirebaseDatabase
                .getInstance()
                .getReference(PRIVATE_FOLDERS_CONTENTS)
                .child(user)
                .child(ClassContentsMainActivity.projectKey);

        mPrivateFullImageDatabaseRef.keepSynced(true);

    }

    @Override
    public void onResume() {
        super.onResume();
            AppUsageAnalytics.incrementPageVisitCount("StackImages");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
