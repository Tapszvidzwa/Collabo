package com.example.tapiwa.collegebuddy.Main.UserProfile;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.CameraGalleryUploads.NewImage;
import com.example.tapiwa.collegebuddy.Main.HomePage.MainFrontPageActivity;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.CameraGalleryUploads.CameraGalleryUpload;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tapiwa on 10/5/17.
 */

public class UserProfileFragment extends Fragment {

    public static TextView username;
    public static CircleImageView user_profile_photo;
    private View profileView;
    private Button uploadProfilePhotoBtn;
    private DatabaseReference mProfilePicsDBRef;
    private String uid;


    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainFrontPageActivity.CurrentFragment = getString(R.string.profile_fragment);

        profileView = inflater.inflate(R.layout.user_profile_fragment, container, false);
        username = profileView.findViewById(R.id.user_name);
        user_profile_photo = profileView.findViewById(R.id.profile_photo);
        uploadProfilePhotoBtn = profileView.findViewById(R.id.upload_profile_photo_btn);


        FirebaseAuth auth = MainFrontPageActivity.mAuth;
        uid = auth.getCurrentUser().getUid();

        mProfilePicsDBRef = FirebaseDatabase
                .getInstance()
                .getReference(this.getString(R.string.profile_photos));

        setListeners();

        return profileView;
    }


    private void setListeners() {

        mProfilePicsDBRef.child(uid).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null) {
                    return;
                }

                final NewImage prof_pic = dataSnapshot.getValue(NewImage.class);
                Picasso.with(getActivity())
                        .load(prof_pic.getThumb_uri())
                        .fit()
                        .placeholder(R.drawable.ic_user)
                        .priority(Picasso.Priority.HIGH)
                        .into(user_profile_photo, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                // Try again online if cache failed
                                Picasso.with(getActivity())
                                        .load(prof_pic.getThumb_uri())
                                        .fit()
                                        .priority(Picasso.Priority.HIGH)
                                        .into(user_profile_photo);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        uploadProfilePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMenu(uploadProfilePhotoBtn);
            }
        });

    }


    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        // This activity implements OnMenuItemClickListener .
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.toString()) {
                    case "Capture":
                        CameraGalleryUpload.takePicture(getActivity(), getString(R.string.profile_fragment));
                        return true;

                    case "Select from Gallery":
                        CameraGalleryUpload.chooseImageFromGallery(getActivity());
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.inflate(R.menu.upload_profile_pic_menu);
        popup.show();
    }
}

