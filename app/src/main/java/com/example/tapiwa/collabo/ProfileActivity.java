package com.example.tapiwa.collabo;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    private Toolbar mToolBar;
    private TextView mDisplayName, mUserBio;
    private ImageView mProfilePhoto;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceCurrentUser;
    private FirebaseUser mCurrentUser;
    private Button mSendFriendRequestBtn;
    private final String BUDDIE_REQUESTS_SENT = "Friend_Requests_Sent";
    public final static String BUDDIE_REQUESTS_RECEIVED = "Friend_Requests_received";
    private DatabaseReference mBuddieRequestsSentDBRef;
    private DatabaseReference mBuddieRequestsReceivedDBRef;
    private AVLoadingIndicatorView mLoadingSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        final String ReceivedUid = getIntent().getStringExtra("uid");
        String myprofile = getIntent().getStringExtra("myProfile");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mLoadingSpinner = (AVLoadingIndicatorView) findViewById(R.id.request_sent_spinner);
        final String current_user_id = mCurrentUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseReferenceCurrentUser = FirebaseDatabase.getInstance().getReference("Users").child(current_user_id);
        mDatabaseReference.keepSynced(true);

        mBuddieRequestsSentDBRef = FirebaseDatabase.getInstance().getReference().child(BUDDIE_REQUESTS_SENT);
        mBuddieRequestsReceivedDBRef = FirebaseDatabase.getInstance().getReference().child(BUDDIE_REQUESTS_RECEIVED);


        mBuddieRequestsSentDBRef.keepSynced(true);
        mBuddieRequestsReceivedDBRef.keepSynced(true);


        mToolBar = (Toolbar) findViewById(R.id.profile_toolBar);
        mSendFriendRequestBtn = (Button) findViewById(R.id.send_friend_request);
        mUserBio = (TextView) findViewById(R.id.bio);
        mDisplayName = (TextView) findViewById(R.id.DisplayName);
        mProfilePhoto = (ImageView) findViewById(R.id.profile_photo);

        mToolBar.setTitle("User Profile");

        if (myprofile.equals("mine")) {
            setSupportActionBar(mToolBar);

        } else {
            //profile is not mine, check if friend request has been sent
            final Query RequestsQuery = mBuddieRequestsSentDBRef.child(current_user_id).equalTo(ReceivedUid);

            RequestsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChildren()) {
                        disableSendRequestBtn();
                    } else {
                        mSendFriendRequestBtn.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        mSendFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingSpinner.setVisibility(View.VISIBLE);



                mDatabaseReference.child(ReceivedUid).addListenerForSingleValueEvent(new ValueEventListener() {

                    //sent details of user receiving request
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name1 = dataSnapshot.child("name")
                                .getValue().toString() ;
                        String uid1 = dataSnapshot.child("uid")
                                .getValue().toString() ;
                        String bio1 = dataSnapshot.child("bio")
                                .getValue().toString();
                        String image_uri1 = dataSnapshot.child("image_uri")
                                .getValue().toString() ;
                        String thumb_image1 = dataSnapshot.child("thumb_image")
                                .getValue().toString();

                        BuddieProfiles profile1 = new BuddieProfiles(name1, image_uri1, bio1, thumb_image1);


                        mBuddieRequestsSentDBRef
                                .child(current_user_id).child(ReceivedUid)
                                .setValue(profile1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    mDatabaseReferenceCurrentUser
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            String name = dataSnapshot.child("name")
                                                    .getValue()
                                                    .toString() ;
                                            String uid = dataSnapshot.child("uid")
                                                    .getValue().toString() ;
                                            String bio = dataSnapshot.child("bio")
                                                    .getValue().toString();
                                            String image_uri= dataSnapshot.child("image_uri")
                                                    .getValue().toString() ;
                                            String thumb_image= dataSnapshot.child("thumb_image")
                                                    .getValue().toString();

                                            BuddieProfiles profile = new BuddieProfiles(name, image_uri, bio, thumb_image);

                                            mBuddieRequestsReceivedDBRef
                                                    .child(ReceivedUid)
                                                    .child(current_user_id).
                                                    setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        disableSendRequestBtn();
                                                        Toast toast = Toast.makeText(ProfileActivity.this, "Friend request sent... ", Toast.LENGTH_SHORT);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                } else {
                                    mLoadingSpinner.setVisibility(View.INVISIBLE);
                                    Toast toast = Toast.makeText(ProfileActivity.this, "Failed to send friend request... ", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });


        //todo Implement Cancel Friend Request
        //todo save user bio and name and etc in the shared preferences

        mDatabaseReference.child(ReceivedUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserBio.setText(dataSnapshot.child("bio").getValue().toString());
                mDisplayName.setText(dataSnapshot.child("name").getValue().toString());

                final String profile_photo_uri = dataSnapshot.child("image_uri").getValue().toString();

                Picasso.with(ProfileActivity.this)
                        .load(profile_photo_uri)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.new_default_image)
                        .into(mProfilePhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                // Try again online if cache failed
                                Picasso.with(ProfileActivity.this)
                                        .load(profile_photo_uri)
                                        .placeholder(R.drawable.new_default_image)
                                        .into(mProfilePhoto);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void disableSendRequestBtn() {
        mLoadingSpinner.setVisibility(View.INVISIBLE);
        mSendFriendRequestBtn.setBackgroundColor(Color.RED);
        mSendFriendRequestBtn.setText("Friend request sent");
        mSendFriendRequestBtn.setEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_menu, menu);
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
        if (id == R.id.change_bio) {
            Intent changeBio = new Intent(ProfileActivity.this, ChangeBioActivity.class);
            startActivity(changeBio);
        }

        if (id == R.id.change_display_name) {
            Intent changeDisplayName = new Intent(ProfileActivity.this, ChangeDisplayNameActivity.class);
            startActivity(changeDisplayName);
        }

        if (id == R.id.change_profile_photo) {
            Intent changeProfilePhoto = new Intent(ProfileActivity.this, ChangePhotoActivity.class);
            startActivity(changeProfilePhoto);
        }

        return super.onOptionsItemSelected(item);
    }


}
