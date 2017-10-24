package com.example.tapiwa.collegebuddy.Main;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.authentication.LoginActivity;
import com.example.tapiwa.collegebuddy.authentication.NewUser;
import com.example.tapiwa.collegebuddy.classContents.classContentsMain.ClassContentsMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tapiwa on 10/5/17.
 */

public class UserProfileFragment extends Fragment {

        public static TextView username;
        public static CircleImageView user_profile_photo;
        private View profileView;
    private DatabaseReference mUsersDbRef;


        public UserProfileFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

            profileView =  inflater.inflate(R.layout.user_profile_fragment, container, false);


            username = (TextView) profileView.findViewById(R.id.user_name);
            user_profile_photo = (CircleImageView) profileView.findViewById(R.id.profile_photo);


            FirebaseAuth auth = MainFrontPage.mAuth;
            String uid = auth.getCurrentUser().getUid();

            mUsersDbRef = FirebaseDatabase
                    .getInstance()
                    .getReference("Users")
                    .child(uid);

            mUsersDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final NewUser user = dataSnapshot.getValue(NewUser.class);
                    username.setText(user.name);


                    if(!user.image_uri.equals("default")) {

                        Picasso.with(getActivity())
                                .load(user.getImage_uri())
                                .placeholder(R.drawable.ic_user)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .priority(Picasso.Priority.HIGH)
                                .into(user_profile_photo, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                        // Try again online if cache failed
                                        Picasso.with(getActivity())
                                                .load(user.getImage_uri())
                                                .priority(Picasso.Priority.HIGH)
                                                .into(user_profile_photo);
                                    }
                                });
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });





            return profileView;
        }


    }

