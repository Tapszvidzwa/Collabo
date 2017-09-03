package com.example.tapiwa.collabo;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import id.zelory.compressor.Compressor;


public class GroupChatsActivity extends AppCompatActivity {

    private FloatingActionButton addPhotoFab, addPhotoFromGallery;
    private GridView groupImagesGridView;
    private GenericServices internet;
    private Toolbar mToolBar;
    public static DatabaseReference mTagsDatabaseReference;
    public static final String TAGS_DB_PATH = "Tags";
    public static StorageReference mTagsPhotosDBReference;
    public static StorageReference mTagsThumbsUriStorageReference;
    public static DatabaseReference mDatabaseReference;
    public static DatabaseReference mTagsThumbsUriDBReference;
    public static DatabaseReference mChatRoomNamesDatabaseReference;
    public static final String CHAT_ROOMS_DB_REF = "Chat_Room_Names";
    public static final String TAGS_URI_DB_PATH = "GROUP_TAGS_THUMB_URI";
    public static final String TAGS_URI_STORAGE_PATH = "GROUP_TAGS_THUMB_PHOTOS";
    public static final String TAGS_PHOTOS_STORAGE_PATH = "GROUP_TAG_PHOTOS";

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10101;
    private static final int PICK_IMAGE = 12341;

    private final int REQUEST_IMAGE_CAPTURE = 102;
    private Uri fileUri;
    private ArrayList<ImageUpload> FirebaseTagslist;
    private GroupImagesAdapter mAdapter;
    private String mCurrentUser;
    public String thumb_download_url;
    public String groupName;
    public File photoFile;
    public String currentUserName;
    public String groupKey;
    public String chat_room_key;
    private FirebaseUser user;
    private File thumb_file_path;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_chats);

        groupKey = getIntent().getStringExtra("groupKey");
        groupName = getIntent().getStringExtra("groupName");
        internet = new GenericServices(getApplicationContext());


        mToolBar = (Toolbar) findViewById(R.id.tags_toolbar);
        mToolBar.setTitle(groupName);
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAdapter = new GroupImagesAdapter(getApplicationContext(), R.layout.activity_group_chats_item_list, FirebaseTagslist);
        addPhotoFab = (FloatingActionButton) findViewById(R.id.tags_take_photo_flBtn);
        addPhotoFromGallery = (FloatingActionButton) findViewById(R.id.choose_group_image_from_gallery);
        groupImagesGridView = (GridView) findViewById(R.id.tags_list);
        FirebaseTagslist = new ArrayList<>();

        mChatRoomNamesDatabaseReference = FirebaseDatabase.getInstance().getReference().child(CHAT_ROOMS_DB_REF);
        mTagsDatabaseReference = FirebaseDatabase.getInstance().getReference().child(TAGS_DB_PATH).child(groupKey);
        mTagsPhotosDBReference = FirebaseStorage.getInstance().getReference().child(TAGS_PHOTOS_STORAGE_PATH).child(groupKey);
        mTagsThumbsUriDBReference = FirebaseDatabase.getInstance().getReference().child(TAGS_URI_DB_PATH).child(groupKey);
        mTagsPhotosDBReference = FirebaseStorage.getInstance().getReference().child(TAGS_PHOTOS_STORAGE_PATH).child(groupKey);
        mTagsThumbsUriStorageReference = FirebaseStorage.getInstance().getReference().child(TAGS_URI_STORAGE_PATH).child(groupKey);


        mTagsDatabaseReference.keepSynced(true);
        mTagsThumbsUriDBReference.keepSynced(true);


        //Get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUser = user.getUid().toString();



        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser);


 mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
     @Override
     public void onDataChange(DataSnapshot dataSnapshot) {
         NewUser user = dataSnapshot.getValue(NewUser.class);
         currentUserName = user.getName();
     }

     @Override
     public void onCancelled(DatabaseError databaseError) {

     }
 });




     //   currentUserName = "Tapiwa";

        //// TODO: 8/6/17 deal with this part later

        /*

        mDatabaseReferenceQuery = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser);

        mDatabaseReferenceQuery.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */



        groupImagesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent openMaximizeImage = new Intent(GroupChatsActivity.this, MaximizeGroupImageActivity.class);

                ImageUpload clickedItem =  (ImageUpload) mAdapter.getItem(position);


                openMaximizeImage.putExtra("image", clickedItem.getFull_image_uri());
                openMaximizeImage.putExtra("thumbUri", clickedItem.getThumb_uri());
                openMaximizeImage.putExtra("title", clickedItem.getTag());
                openMaximizeImage.putExtra("time", clickedItem.getTimeUploaded());
                openMaximizeImage.putExtra("chat_room_key", clickedItem.getChat_room_key());
                openMaximizeImage.putExtra("administrator", clickedItem.getUploaderUid());

                openMaximizeImage.putExtra("currentUserName", currentUserName);


                startActivity(openMaximizeImage);


            }
        });





        mTagsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch from firebase
                FirebaseTagslist.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    ImageUpload tags = Snapshot1.getValue(ImageUpload.class);
                    FirebaseTagslist.add(tags);
                }

                mAdapter = new GroupImagesAdapter(getApplicationContext(), R.layout.activity_group_chats_item_list, FirebaseTagslist);
                groupImagesGridView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        addPhotoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (internet.isConnectingToInternet()) {
                    TakePicture();
                } else {
                    return;
                }
            }
        });

        addPhotoFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });


    }


    public static String getTime() {

        DateTime dt = new DateTime();
        String timeNow = dt.toString().substring(11, 16);
        int month = dt.monthOfYear().get();
        String date = dt.dayOfMonth().getAsShortText();


        return "(" + month + "/" + date + ")" + " " + timeNow;
    }


    public void startUpload(final String callingfunction) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_keyboard_black_24dp);
        builder.setTitle(("Provide the image label"));

        int maxLength = 40;
        final EditText tag = new EditText(getApplicationContext());
        tag.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        tag.setTextColor(Color.BLACK);
        tag.setVisibility(View.VISIBLE);
        tag.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(tag);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: 7/3/17 fix such that user cannot enter empty tag
                  attemptImageUpload(tag.getText().toString(), callingfunction);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(getApplicationContext(), "Upload cancelled, no tag", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //   BitmapFactory.Options bmOptions = null;
            //   bmOptions = new BitmapFactory.Options();
            //    returnedBitmap = BitmapFactory.decodeFile(photoFile.getPath(), bmOptions);
            startUpload("Capture");
        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            try {
                photoFile = createImageFile();
                fileUri = data.getData();
                thumb_file_path = new File(fileUri.toString());
                startUpload("imagePick");
            } catch (Exception e) {
            }
        }
    }

    public void chooseImageFromGallery() {
        if(checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");
            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
            startActivityForResult(chooserIntent, PICK_IMAGE);
        }
    }






    private void attemptImageUpload(final String tag, String callingFunction) {

        Toast.makeText(getApplicationContext(), "Creating, discussion will be added shortly...", Toast.LENGTH_SHORT).show();

        //Upload the picture to the Photo folder in the Storage bucket
        //// TODO: 6/29/17 change the uri so that its custom for every photo


        try {
            //upload the thumb_uri
                Bitmap thumb_bitmap;

                if(callingFunction.equals("imagePick")) {

                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(
                                fileUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    thumb_bitmap = BitmapFactory.decodeStream(imageStream);

                } else {

                    thumb_bitmap = new Compressor(getApplicationContext())
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(60)
                            .compressToBitmap(photoFile);
                }


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            final byte[] thumb_byte = baos.toByteArray();

            //// TODO: 8/6/17 make it such that the tag is unique
            StorageReference thumb_filePath = mTagsThumbsUriStorageReference;

            //upload the thumbnail to storage
            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
            uploadTask.addOnCompleteListener
                    (new OnCompleteListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onComplete
                                 (@NonNull Task<UploadTask.TaskSnapshot> thumb_image_storage_task) {

                             if (thumb_image_storage_task.isSuccessful()) {
                                 //thumbnail uploaded successfully
                                 @SuppressWarnings("VisibleForTests") String thumb_dwnld_uri = thumb_image_storage_task
                                         .getResult()
                                         .getDownloadUrl()
                                         .toString();

                                 thumb_download_url = thumb_dwnld_uri;
                             }
                         }
                     }
                    );


        } catch (Exception e) {
            e.printStackTrace();
        }


        //Upload the full image uri to the db and also to storage bucket

        StorageReference filepath = mTagsPhotosDBReference.child(fileUri.getLastPathSegment());
        filepath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(getApplicationContext(), "Uploading finished", Toast.LENGTH_SHORT).show();

                 chat_room_key = mTagsDatabaseReference.push().getKey();



                //send notifications to all the users in the group
           /*    try {
                    sendNotifications(username, image_tag, key);
                } catch (IOException e) {
                    e.printStackTrace();
                } */

               @SuppressWarnings("VisibleForTests") String full_image_uri = taskSnapshot.getDownloadUrl().toString();

                ImageUpload imageUpload = new ImageUpload(currentUserName, tag, full_image_uri, getTime(), chat_room_key, thumb_download_url, user.getUid().toString());

                //save image info into the firebase database
                mTagsDatabaseReference.child(chat_room_key).setValue(imageUpload);
                mChatRoomNamesDatabaseReference.child(chat_room_key).child("chat_room_name").setValue(tag);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Discussion creating failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


            private void TakePicture() {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                "com.example.android.fileprovider",
                                photoFile);

                        fileUri = photoURI;
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }


            private File createImageFile() throws IOException {
                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File image = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );

                return image;
            }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // do your stuff
                } else {
                    Toast.makeText(GroupChatsActivity.this, "Denied Access",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }



    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    showDialog("External storage", context,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }


    public void showDialog(final String msg, final Context context,
                           final String permission) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setIcon(R.drawable.ic_perm_media_black_24dp);
        alertBuilder.setTitle("Access to Gallery Permission");
        alertBuilder.setMessage("Permission is necessary to select an image");
        alertBuilder.setPositiveButton("Give permission",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });

        alertBuilder.setCancelable(true);

        AlertDialog alert = alertBuilder.create();
        alert.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.group_contents_activity_menu, menu);

        SearchView searchView;

        MenuItem searchItem = menu.findItem(R.id.search_group_image);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchImage(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchImage(newText);
                return false;
            }
        });


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
        if (id == R.id.group_contents_activity_information) {
            showInformation();
        }


        return super.onOptionsItemSelected(item);
    }

    private void showInformation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatsActivity.this);
        builder.setTitle("Information");
        builder.setMessage(R.string.group_folder_information);
        builder.setIcon(R.drawable.ic_info_black_24dp);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void searchImage(String imageLabel) {

        Query searchImage = mTagsDatabaseReference.orderByChild("tag")
                .startAt(imageLabel)
                .endAt(imageLabel + "\uf8ff");


        searchImage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                FirebaseTagslist.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    ImageUpload privateImage = Snapshot1.getValue(ImageUpload.class);
                    FirebaseTagslist.add(privateImage);
                }

                mAdapter = new GroupImagesAdapter(getApplicationContext(), R.layout.activity_group_chats_item_list, FirebaseTagslist);
                groupImagesGridView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }





    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }




}
