package com.example.tapiwa.collabo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import id.zelory.compressor.Compressor;


public class ChangePhotoActivity extends AppCompatActivity {


    private ProgressDialog mProgress;
    private Button mPickFromGallery, mTakeNewPhoto;
    private Toolbar mToolbar;
    private StorageReference mStorageReference;
    private FirebaseUser user;
    private DatabaseReference mUsersDBReference;
    private final static int CHOOSE_FROM_GALLERY = 102;
    private final static String PROFILE_PHOTOS_PATH = "ProfilePhotos";
    private final static String PROFILE_PHOTO_THUMBNAILS = "ProfileThumbNails";
    private final static int REQUEST_IMAGE_CAPTURE = 103;
    private String uid;
    private Uri captured_img_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_photo);

        mToolbar = (Toolbar) findViewById(R.id.change_photo_toolbar);
        mProgress = new ProgressDialog(ChangePhotoActivity.this);
        mPickFromGallery = (Button) findViewById(R.id.choose_from_gallery);
        mTakeNewPhoto = (Button) findViewById(R.id.capture_new_photo);


        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mToolbar.setTitle("Change Profile Photo");
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mUsersDBReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mUsersDBReference.keepSynced(true);


        mPickFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openGallery = new Intent();
                openGallery.setType("image/*");
                openGallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(openGallery, "SELECT IMAGE"), CHOOSE_FROM_GALLERY);

            }
        });

        mTakeNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 8/1/17 fix take picture button
            }
        });

    }

    private void TakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(ChangePhotoActivity.this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(ChangePhotoActivity.this,
                        "com.example.android.fileprovider",
                        photoFile);

                captured_img_uri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = ChangePhotoActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri imageUri = captured_img_uri;
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();


                mProgress.setTitle("Uploading Photo");
                mProgress.setMessage("Please wait while we upload profile photo... ");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();


                try {

                    File thumb_filePath = new File(resultUri.getPath());

                  //  Bitmap thumb_bitmap = SiliCompressor.with(ChangePhotoActivity.this).getCompressBitmap(resultUri.toString());

                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(70)
                            .compressToBitmap(thumb_filePath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    StorageReference filepath = mStorageReference
                            .child(PROFILE_PHOTOS_PATH).child(uid);
                    final StorageReference thumb_filepath = mStorageReference
                            .child(PROFILE_PHOTO_THUMBNAILS).child(uid);

                    //upload full cropped image
                    filepath.putFile(resultUri)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask
                                        .TaskSnapshot> full_image_storage) {
                                    if (full_image_storage.isSuccessful()) {


                                        @SuppressWarnings("VisibleForTests") final String full_image_download_url = full_image_storage
                                                .getResult()
                                                .getDownloadUrl()
                                                .toString();

                                        //upload full image uri
                                        mUsersDBReference
                                                .child("image_uri")
                                                .setValue(full_image_download_url).addOnCompleteListener(
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> full_image_url_Uploaded_task) {
                                                        if (full_image_url_Uploaded_task.isSuccessful()) {

                                                            //upload the thumbnail to storage
                                                            UploadTask uploadTask = thumb_filepath
                                                                    .putBytes(thumb_byte);
                                                            uploadTask.addOnCompleteListener
                                                                    (new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                         @Override
                                                                         public void onComplete
                                                                                 (@NonNull Task<UploadTask.TaskSnapshot> thumb_image_storage_task) {

                                                                             if (thumb_image_storage_task.isSuccessful()) {
                                                                                 //thumbnail uploaded successfully
                                                                                 @SuppressWarnings("VisibleForTests") String thumb_download_url = thumb_image_storage_task
                                                                                         .getResult()
                                                                                         .getDownloadUrl()
                                                                                         .toString();

                                                                                 //upload the thumbnail uri to database
                                                                                 mUsersDBReference
                                                                                         .child("thumb_image").setValue(thumb_download_url)
                                                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                             @Override
                                                                                             public void onComplete(@NonNull Task<Void> thumb_database_task) {
                                                                                                 if (thumb_database_task.isSuccessful()) {

                                                                                                     mProgress.dismiss();
                                                                                                     Toast toast = Toast.makeText(ChangePhotoActivity.this, "Profile photo updated successfully... ", Toast.LENGTH_SHORT);
                                                                                                     toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                                     toast.show();
                                                                                                     finish();

                                                                                                 }
                                                                                             }
                                                                                         });
                                                                             }

                                                                         }
                                                                     }
                                                                    );
                                                        }
                                                    }
                                                });
                                    }

                                }
                            });

                } catch (IOException e) {
                    e.printStackTrace();

                    mProgress.dismiss();
                    Toast toast = Toast.makeText(ChangePhotoActivity.this, "Failed to update profile photo... ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    return;
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                mProgress.dismiss();
                Toast toast = Toast.makeText(ChangePhotoActivity.this, "Failed to update profile photo... ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }


        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
