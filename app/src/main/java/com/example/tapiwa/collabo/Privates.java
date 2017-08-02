package com.example.tapiwa.collabo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class Privates extends Fragment {

    public Privates() {
    }

    public ListView listview;
    Vibrator vibrate;
    public ArrayList<ImageUpload> list;
    public PrivateTagListAdapter adapter;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private ProgressDialog mProgress;
    private StorageReference storageReference;
    private String image_tag;
    final int REQUEST_IMAGE_CAPTURE = 1;
    SharedPreferences usrName;
    private String user;
    Uri fileUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid().toString();

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        mProgress = new ProgressDialog(getContext());
        usrName = PreferenceManager.getDefaultSharedPreferences(getContext());


        View privateTags = inflater.inflate(R.layout.privatetags, container, false);
        listview = (ListView) privateTags.findViewById(R.id.unopenedlistView);
        list = new ArrayList<>();
        adapter = new PrivateTagListAdapter(getContext(), R.layout.privatetag_item_list, list);
        listview.setAdapter(adapter);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(user);
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


                //init unopenedMessagesadapter
                adapter = new PrivateTagListAdapter(getContext(), R.layout.privatetag_item_list, list);
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
                intent.putExtra("activityCalling", "privates");
                intent.putExtra("name", item.getProfileName());
                intent.putExtra("time", item.getTimeUploaded());
                intent.putExtra("user", user);

                //Start details activity
                startActivity(intent);
            }
        });


        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           int position, long id) {

                final ImageUpload item = (ImageUpload) parent.getItemAtPosition(position);
               // vibrate.vibrate(40);

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }
                builder.setTitle("Delete Private Image")
                        .setMessage("Are you sure you want to delete this Image?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                //// TODO: 7/11/17 Please refactor this long code and also try to implement it in FirebaseHelper
                                String imageUri = item.getUrl();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.keepSynced(true);
                                final StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri);
                                final Query ImagesQuery = ref.child(user).orderByChild("url").equalTo(imageUri);


                                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Image successfully deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(getContext(), "Image failed to delete" + exception, Toast.LENGTH_LONG).show();

                                    }
                                });

                                ImagesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                                            Snapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(getContext(), "Failed to delete Image", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });


        FloatingActionButton takePhoto = (FloatingActionButton) privateTags.findViewById(R.id.capturePrivatePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TakePicture();
            }
        });

        FloatingActionButton openNotes = (FloatingActionButton) privateTags.findViewById(R.id.openNotes);
        openNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notes = new Intent(getActivity(), Notes.class);
                startActivity(notes);
            }
        });





        return privateTags;
    }



    public void startUpload() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(("Provide the tag"));

        int maxLength = 40;
        final EditText tag = new EditText(getContext());
        tag.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        tag.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(tag);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: 7/3/17 fix such that user cannot enter empty tag
                image_tag = tag.getText().toString();
                attemptImageUpload();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(getContext(), "Upload cancelled, no tag", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            startUpload();
        }
    }

    private void attemptImageUpload() {
        mProgress.setMessage("Uploading Private Image...");
        mProgress.show();
        //Upload the picture to the Photo folder in the Storage bucket
        //// TODO: 6/29/17 change the uri so that its custom for every photo


        StorageReference filepath = storageReference.child(user).child(fileUri.getLastPathSegment());
        filepath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress.dismiss();

                Toast.makeText(getContext(), "Uploading finished", Toast.LENGTH_SHORT).show();


                @SuppressWarnings("VisibleForTests") String url = taskSnapshot.getDownloadUrl().toString();
                ImageUpload imageUpload = new ImageUpload(image_tag, url, Collabos.getTime());

                //save image info into the firebase database
                String uploadId = mDatabaseRef.push().getKey();
                mDatabaseRef.child(uploadId).setValue(imageUpload);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgress.dismiss();
                Toast.makeText(getContext(), "Uploading failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private  void TakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);

                fileUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private  File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }


}