package com.example.tapiwa.collabo;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class MaximizeGroupImageActivity extends AppCompatActivity {


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
    private String imgThumbUri, fullImageUri;
    private String administrator;
    private GenericServices internet;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maximize_group_image);




        final String imageUri = getIntent().getStringExtra("image");
        final String thumbUri = getIntent().getStringExtra("thumbUri");
        administrator = getIntent().getStringExtra("administrator");

        imgThumbUri = thumbUri;
        fullImageUri = imageUri;

       final String title = getIntent().getStringExtra("title");
        String timegiven = getIntent().getStringExtra("time");
        chat_room_key = getIntent().getStringExtra("chat_room_key");
        profilename = getIntent().getStringExtra(("name"));
        current_user_name = getIntent().getStringExtra("currentUserName");
       final String chatRoom = getIntent().getStringExtra(("chatRoom"));




        mToolbar = (Toolbar) findViewById(R.id.maximize_image_toolbar);
        mToolbar.setTitle(title);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        open_discussion = (Button) findViewById(R.id.open_chat_room_btn);


        timeuploaded = (TextView) findViewById(R.id.timeImageUploaded);
        name = (TextView) findViewById(R.id.profileName);
        imageView = (ZoomageView) findViewById(R.id.full_image_display);

        name.setText(profilename);
        timeuploaded.setText(timegiven);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);


     //   final StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri);
   //     final Query ImagesQuery = ref.child(FB_DATABASE_PATH).orderByChild("full_image_uri").equalTo(imageUri);

        /*
//// TODO: 7/11/17 Implement this method in the firebase helper
        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 //// TODO: 7/11/17 make sure it deletes from Storage too and give result to UI

                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        Toast.makeText(MaximizeImage.this, "Collabo successfully deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MaximizeImage.this, "Collabo failed to delete" + exception, Toast.LENGTH_LONG).show();
             
                    }
                });

                ImagesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                            Snapshot.getRef().removeValue();
                        }
                        MaximizeImage.this.finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MaximizeImage.this, "Failed to delete Image", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        */


        open_discussion.setOnClickListener(new View.OnClickListener() {
            @Override
         public void onClick(View v) {
                openDiscussion();
                     }
            });



        Picasso.with(this)
                .load(imageUri)
                .rotate(90)
                .priority(Picasso.Priority.HIGH)
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
            //// TODO: 8/10/17 display are you sure you want to delete here

        }

        if(id == R.id.rotate_private_maximized_image_icon) {
            imageView.setRotation(imageView.getRotation() + 90);
        }

        if(id == R.id.cancel_private_maximized_image) {
            this.finish();
        }

        if(id == R.id.delete_maximized_image_icon) {
            continueWithDeleteDialogue();
        }

        return super.onOptionsItemSelected(item);
    }

    private void continueWithDeleteDialogue() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(("Delete Discussion"));
        builder.setMessage("Are you sure you want to delete discussion? This will delete all the chats in this discussion.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteDiscussion();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }


    private void deleteDiscussion() {

        //delete the images in the storage

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String thisUser = currentUser.getUid().toString();

        if(!thisUser.equals(administrator)) {

            Toast.makeText(MaximizeGroupImageActivity.this, "Only the uploader of this discussion can delete it", Toast.LENGTH_SHORT).show();
            return;
        } else {

            this.finish();

            GroupChatsActivity.mTagsDatabaseReference.child(chat_room_key).removeValue();
            GroupChatsActivity.mChatRoomNamesDatabaseReference.child(chat_room_key).removeValue();
            DatabaseReference mChatRoomDatabaseRoot = FirebaseDatabase
                    .getInstance()
                    .getReference();

            mChatRoomDatabaseRoot.child(ChatActivity.CHAT_ROOM_CHATS_DB_PATH)
                    .child(chat_room_key).removeValue();

            StorageReference tagFullImageStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(fullImageUri);
            tagFullImageStorageRef.delete();

            StorageReference tagThumbStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgThumbUri);
            tagThumbStorageRef.delete();

        }

    }

    private void openDiscussion() {

        //Pass the image title and full_image_uri to DetailsActivity
        Intent intent = new Intent(MaximizeGroupImageActivity.this, ChatActivity.class);
        intent.putExtra("chat_room_key", chat_room_key);
        intent.putExtra("current_user_name", current_user_name);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up );

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
