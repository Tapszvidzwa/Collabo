package com.example.tapiwa.collabo;


import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class MaximizePrivateImageActivity extends AppCompatActivity {


    private TextView tag;
    private TextView timeuploaded;
    private ZoomageView imageView;
    private TextView name;
    private ProgressBar progressBar;
    private FloatingActionButton deletePhoto;
    private String FB_DATABASE_PATH;
    private String chat_room_key;
    private String profilename;
    private Toolbar mToolbar;
    private String current_user_name;
    private Button open_discussion;
    private String openedFullImageUri;
    private String openedImageThumbUri;
    private String imageKey;
    private FirebaseStorage mStorage;
    private Boolean goAheadDelete;
    private int mCurrentRotation = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maximize_private_image);

        final String imageUri = getIntent().getStringExtra("imageUri");
        final String thumbUri = getIntent().getStringExtra("thumbUri");
        imageKey = getIntent().getStringExtra("imageKey");

        openedFullImageUri = imageUri;
        openedImageThumbUri = thumbUri;

        goAheadDelete = false;

        mToolbar = (Toolbar) findViewById(R.id.maximize_private_image_toolbar);
        mToolbar.setTitle("Private Image");
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageView = (ZoomageView) findViewById(R.id.full_private_image_display);

        mStorage = FirebaseStorage.getInstance();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);


        Picasso.with(this)
                .load(imageUri)
                .priority(Picasso.Priority.HIGH)
                .rotate(270)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        // Try again online if cache failed
                        Picasso.with(getApplicationContext())
                                .load(imageUri)
                                .rotate(90)
                                .priority(Picasso.Priority.HIGH)
                                .into(imageView);
                    }
                    });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.maximize_private_image_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Main/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        //// TODO: 8/1/17 Change these settings to custom settings
        if (id == R.id.delete_maximized_image_icon) {
                continueDeleteDialogue();
        }

        if(id == R.id.rotate_private_maximized_image_icon) {
            imageView.setRotation(imageView.getRotation() + 90);
        }

        if(id == R.id.cancel_private_maximized_image) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
    
    
    

    public void deleteImage() {

            //get the database reference for image
            //delete the from the database
            DatabaseReference fullImageRef = PrivateFolderContentsActivity.mPrivateFullImageDatabaseRef.child(imageKey).getRef();
            fullImageRef.removeValue();
            this.finish();

           //get the full image storage reference
            //delete from the storage
            StorageReference PrivateFullImageStorageRef = mStorage.getReferenceFromUrl(openedFullImageUri);
            PrivateFullImageStorageRef.delete();

            //get the thumb nail storage reference
            //delete from the thumb nail storage reference
            StorageReference ThumbNailStorageRef = mStorage.getReferenceFromUrl(openedImageThumbUri);
            ThumbNailStorageRef.delete();

    }


    public void continueDeleteDialogue() {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       deleteImage();
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

}
