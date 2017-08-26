package com.example.tapiwa.collabo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import id.zelory.compressor.Compressor;


public class GroupChatsActivity extends AppCompatActivity {

    private Button startNewDiscussion;
    private ListView TagsList;
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

    private final int REQUEST_IMAGE_CAPTURE = 102;
    private Uri fileUri;
    private ArrayList<ImageUpload> FirebaseTagslist;
    private TagsAdapter mAdapter;
    private String mCurrentUser;
    public String thumb_download_url;
    public String groupName;
    public File photoFile;
    public String currentUserName;
    public String groupKey;
    public String chat_room_key;
    private FirebaseUser user;


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

        mAdapter = new TagsAdapter(getApplicationContext(), R.layout.activity_group_chats_item_list, FirebaseTagslist);
        startNewDiscussion = (Button) findViewById(R.id.tags_take_photo_flBtn);
        TagsList = (ListView) findViewById(R.id.tags_list);
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



        TagsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

                mAdapter = new TagsAdapter(getApplicationContext(), R.layout.activity_group_chats_item_list, FirebaseTagslist);
                TagsList.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        startNewDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (internet.isConnectingToInternet()) {
                    TakePicture();
                } else {
                    return;
                }
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


    public void startUpload() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(("Provide the Collabo tag"));

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
                  attemptImageUpload(tag.getText().toString());
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
            startUpload();
        }
    }

    private void attemptImageUpload(final String tag) {

        Toast.makeText(getApplicationContext(), "Creating, discussion will be added shortly...", Toast.LENGTH_SHORT).show();

        //Upload the picture to the Photo folder in the Storage bucket
        //// TODO: 6/29/17 change the uri so that its custom for every photo


        try {
            //upload the thumb_uri

            Bitmap thumb_bitmap = new Compressor(this)
                    .setMaxHeight(100)
                    .setMaxWidth(100)
                    .setQuality(60)
                    .compressToBitmap(photoFile);

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

                ImageUpload imageUpload = new ImageUpload(mCurrentUser, tag, full_image_uri, getTime(), chat_room_key, thumb_download_url, user.getUid().toString());

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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }




}
