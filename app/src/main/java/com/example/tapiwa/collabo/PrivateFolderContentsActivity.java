package com.example.tapiwa.collabo;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import id.zelory.compressor.Compressor;

public class PrivateFolderContentsActivity extends AppCompatActivity {


    private DatabaseReference mPrivateFolderContentsRef;
    public GridView ImagesGridView;
    Vibrator vibrate;
    public ArrayList<ImageUpload> list;
    public static StorageReference privateThumbNailsStorageRef;
    public static String PRIVATE_IMAGES_THUMBNAILS ="Private_Images_Thumbnails";
    public PrivateFolderContentsAdapter adapter;
    public static DatabaseReference mPrivateFullImageDatabaseRef;
    private CardView mCardView;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    public static final String PRIVATE_FOLDERS_CONTENTS = "Private_Folders_Contents";
    public static final String PRIVATE_FOLDER_CONTENTS_IMAGE_STORAGE_PATH = "Private_Folders_Photos";
    private ProgressDialog mProgress;
    public static StorageReference mPrivateFullImageStorageRef;
    private String image_tag;
    private String thumb_download_url;
    private  File photoFile = null;
    final int REQUEST_IMAGE_CAPTURE = 1;
    private String user;
    private Toolbar mToolbar;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_folder_contents);

        final String projectKey = getIntent().getStringExtra("projectKey");

        ImagesGridView = (GridView) findViewById(R.id.private_folders_grdV);
        mToolbar = (Toolbar) findViewById(R.id.private_folder_contents_toolbar);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid().toString();
        vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mStorage = FirebaseStorage.getInstance();

        mPrivateFullImageStorageRef = mStorage.getReference(PRIVATE_FOLDER_CONTENTS_IMAGE_STORAGE_PATH).child(user);
        mProgress = new ProgressDialog(getApplicationContext());
        privateThumbNailsStorageRef = FirebaseStorage.getInstance().getReference().child(PRIVATE_IMAGES_THUMBNAILS).child(user).child(projectKey);
        list = new ArrayList<>();


        registerForContextMenu(ImagesGridView);
        mToolbar.setTitle("Uploaded content");
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        adapter = new PrivateFolderContentsAdapter(PrivateFolderContentsActivity.this, R.layout.private_folder_contents_item_lst, list);
        ImagesGridView.setAdapter(adapter);

        mPrivateFullImageDatabaseRef = FirebaseDatabase.getInstance().getReference(PRIVATE_FOLDERS_CONTENTS).child(user).child(projectKey);
        mPrivateFullImageDatabaseRef.keepSynced(true);



        mPrivateFullImageDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                list.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    ImageUpload privateImage = Snapshot1.getValue(ImageUpload.class);
                    list.add(privateImage);
                }
                Collections.reverse(list);


                adapter = new PrivateFolderContentsAdapter(getApplicationContext(), R.layout.private_folder_contents_item_lst, list);
                ImagesGridView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });




        ImagesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ImageUpload imageUpload =  (ImageUpload) adapter.getItem(position);


                Intent openMaximizedImage = new Intent(PrivateFolderContentsActivity.this, MaximizePrivateImageActivity.class);
                openMaximizedImage.putExtra("imageUri", imageUpload.getFull_image_uri());
                openMaximizedImage.putExtra("thumbUri", imageUpload.getThumb_uri());
                openMaximizedImage.putExtra("imageKey", imageUpload.getImage_key());
                startActivity(openMaximizedImage);
            }
        });

        ImagesGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                ImageUpload imageUpload =  (ImageUpload) adapter.getItem(position);
                vibrate.vibrate(50);

                String imageUri = imageUpload.getFull_image_uri();
                String thumbUri = imageUpload.getThumb_uri();
                String imageKey = imageUpload.getImage_key();
                continueDeleteDialogue(imageKey, imageUri, thumbUri);

                return true;
            }
        });




        FloatingActionButton takePhoto = (FloatingActionButton) findViewById(R.id.capturePrivatePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TakePicture();
            }
        });

    }


    public void startUpload() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(("Enter the image label"));

        int maxLength = 40;
        final EditText tag = new EditText(getApplicationContext());
        tag.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        tag.setInputType(InputType.TYPE_CLASS_TEXT);
        tag.setTextColor(Color.BLACK);
        tag.setVisibility(View.VISIBLE);
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


        Toast.makeText(getApplicationContext(), "Uploading... Image will be added once done", Toast.LENGTH_SHORT).show();

        //Upload the picture to the Photo folder in the Storage bucket
        //// TODO: 6/29/17 change the uri so that its custom for every photo
        try {
            //upload the thumb_uri
            Bitmap thumb_bitmap = new Compressor(getApplicationContext())
                    .setMaxHeight(200)
                    .setMaxWidth(200)
                    .setQuality(60)
                    .compressToBitmap(photoFile);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            final byte[] thumb_byte = baos.toByteArray();

            //// TODO: 8/6/17 make it such that the tag is unique
            StorageReference thumb_filePath = privateThumbNailsStorageRef;

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



        StorageReference filepath = mPrivateFullImageStorageRef.child(fileUri.getLastPathSegment());
        filepath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(getApplicationContext(), "Uploading finished", Toast.LENGTH_SHORT).show();

                @SuppressWarnings("VisibleForTests") String url = taskSnapshot.getDownloadUrl().toString();

                //save image info into the firebase database
                String uploadId = mPrivateFullImageDatabaseRef.push().getKey();

                ImageUpload imageUpload = new ImageUpload(image_tag, url, thumb_download_url,  Collabos.getTime(), uploadId);
                mPrivateFullImageDatabaseRef.child(uploadId).setValue(imageUpload);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgress.dismiss();
                Toast.makeText(getApplicationContext(), "Uploading failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private  void TakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
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


    private  File createImageFile() throws IOException {
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




    public void deleteImage(String imageKey, String fullImageUri, String thumbUri) {

        //get the database reference for image
        //delete the from the database
        DatabaseReference fullImageRef = mPrivateFullImageDatabaseRef.child(imageKey).getRef();
        fullImageRef.removeValue();
        this.finish();

        //get the full image storage reference
        //delete from the storage
        StorageReference PrivateFullImageStorageRef = mStorage.getReferenceFromUrl(fullImageUri);
        PrivateFullImageStorageRef.delete();

        //get the thumb nail storage reference
        //delete from the thumb nail storage reference
        StorageReference ThumbNailStorageRef = mStorage.getReferenceFromUrl(thumbUri);
        ThumbNailStorageRef.delete();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.uploaded_content_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_private_image).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchImage(query);
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    private void searchImage(String imageLabel) {

        Query searchImage = mPrivateFullImageDatabaseRef.orderByChild("tag")
                .startAt(imageLabel)
                .endAt(imageLabel + "\uf8ff");


        searchImage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                list.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    ImageUpload privateImage = Snapshot1.getValue(ImageUpload.class);
                    list.add(privateImage);
                }
                Collections.reverse(list);


                adapter = new PrivateFolderContentsAdapter(getApplicationContext(), R.layout.private_folder_contents_item_lst, list);
                ImagesGridView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    public void continueDeleteDialogue(String deleteImageKey, String fullImgUri, String thumbUrl) {

        final String imgKey = deleteImageKey;
        final String fullImageUri = fullImgUri;
        final String thumbUri = thumbUrl;

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
                        deleteImage(imgKey, fullImageUri, thumbUri);
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
