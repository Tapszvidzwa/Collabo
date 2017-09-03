package com.example.tapiwa.collabo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.Manifest;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1002;
    public static StorageReference privateThumbNailsStorageRef;
    public static String PRIVATE_IMAGES_THUMBNAILS ="Private_Images_Thumbnails";
    public PrivateFolderContentsAdapter adapter;
    public static DatabaseReference mPrivateFullImageDatabaseRef;
    private CardView mCardView;
    private FirebaseAuth mAuth;
    private Menu mMenu;
    private SearchView searchView;
    private FirebaseStorage mStorage;
    public static final String PRIVATE_FOLDERS_CONTENTS = "Private_Folders_Contents";
    public static final String PRIVATE_FOLDER_CONTENTS_IMAGE_STORAGE_PATH = "Private_Folders_Photos";
    private ProgressDialog mProgress;
    public static StorageReference mPrivateFullImageStorageRef;
    private String image_tag;
    private String thumb_download_url;
    private  File photoFile = null;
    private final int CAPTURE_IMAGE_PERMISSION = 1003;
    final int REQUEST_IMAGE_CAPTURE = 1;
    private String user;
    private Toolbar mToolbar , mFolderNameToolBar;
    private final int PICK_IMAGE = 1001;
    private boolean useGalleryPermissionGranted = false;
    private boolean useCameraPermissionGranted = false;
    private View parentLayout;
    Uri fileUri;
    File thumb_file_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_folder_contents);

        final String projectKey = getIntent().getStringExtra("projectKey");
        parentLayout = findViewById(R.id.private_folder_contents_parent_layout);


        ImagesGridView = (GridView) findViewById(R.id.private_folders_grdV);

        mFolderNameToolBar = (Toolbar) findViewById(R.id.private_folder_contents_name_toolbar);
        mFolderNameToolBar.setTitle("My Images/" + getIntent().getStringExtra("projectName"));

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid().toString();
        vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mStorage = FirebaseStorage.getInstance();

        mPrivateFullImageStorageRef = mStorage.getReference(PRIVATE_FOLDER_CONTENTS_IMAGE_STORAGE_PATH).child(user);
        mProgress = new ProgressDialog(getApplicationContext());
        privateThumbNailsStorageRef = FirebaseStorage.getInstance().getReference().child(PRIVATE_IMAGES_THUMBNAILS).child(user).child(projectKey);
        list = new ArrayList<>();


        registerForContextMenu(ImagesGridView);
        setSupportActionBar(mFolderNameToolBar);


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

        FloatingActionButton chooseFromGallery = (FloatingActionButton) findViewById(R.id.choose_private_image_from_gallery);

        chooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });

    }


    public void startUpload(final String callingFunction) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(("Enter the image titlehyyy"));
        builder.setIcon(R.drawable.ic_keyboard_black_24dp);

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
                attemptImageUpload(callingFunction);
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
            startUpload("imageCapture");
        }


        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
                    try {
                        photoFile = createImageFile();
                        fileUri = data.getData();
                        thumb_file_path = new File(fileUri.toString());
                        startUpload("imagePick");
                    } catch (Exception e) {
                    }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // do your stuff
                } else {
                    Toast.makeText(PrivateFolderContentsActivity.this, "Denied Access",
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
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
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


    private void attemptImageUpload(String callingFunction) {

        Toast.makeText(getApplicationContext(), "Uploading... Image will be added once done", Toast.LENGTH_SHORT).show();

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
        adapter.notifyDataSetChanged();


      /*  //get the full image storage reference
        //delete from the storage
        StorageReference PrivateFullImageStorageRef = mStorage.getReferenceFromUrl(fullImageUri);
        PrivateFullImageStorageRef.delete();

        //get the thumb nail storage reference
        //delete from the thumb nail storage reference
        StorageReference ThumbNailStorageRef = mStorage.getReferenceFromUrl(thumbUri);
        ThumbNailStorageRef.delete(); */
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.uploaded_content_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_private_image);
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


        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Main/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        //// TODO: 8/1/17 Change these settings to custom settings
        if (id == R.id.search_private_image) {
            // Get the SearchView and set the searchable configuration
           // SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
          //  SearchView searchView = (SearchView) mMenu.findItem(R.id.search_private_image);
            // Assumes current activity is the searchable activity
           // searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
          //  searchView.setIconifiedByDefault(true);
        }

        if (id == R.id.search_private_image_information) {
            showInformation();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInformation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PrivateFolderContentsActivity.this);
        builder.setTitle("Information");
        builder.setMessage(R.string.private_folder_information);
        builder.setIcon(R.drawable.ic_info_black_24dp);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    public void chooseImageFromGallery() {

        if(checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");
            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
            startActivityForResult(chooserIntent, PICK_IMAGE);
        } else {
            Snackbar.make(parentLayout, "You need access to media Gallery to choose images", Snackbar.LENGTH_SHORT).show();
        }
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
