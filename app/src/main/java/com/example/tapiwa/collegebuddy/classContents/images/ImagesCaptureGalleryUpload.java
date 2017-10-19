package com.example.tapiwa.collegebuddy.classContents.images;

import android.Manifest;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.miscellaneous.GenericServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by tapiwa on 10/19/17.
 */

public class ImagesCaptureGalleryUpload extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1002;
    private final int PICK_IMAGE = 1001;
    final int REQUEST_IMAGE_CAPTURE = 1;

    public static FirebaseAuth mAuth;

    public static final String PRIVATE_FOLDERS_CONTENTS = "Private_Folders_Contents";
    public static final String PRIVATE_FOLDER_CONTENTS_IMAGE_STORAGE_PATH = "Private_Folders_Photos";
    public static String PRIVATE_IMAGES_THUMBNAILS = "Private_Images_Thumbnails";

    public static FirebaseStorage mStorage;
    public static StorageReference mPrivateFullImageStorageRef;
    public static StorageReference privateThumbNailsStorageRef;
    public static DatabaseReference mPrivateFullImageDatabaseRef;

    public static String image_tag, thumb_download_url, user;
    public static File thumb_file_path;
    private static Uri resultfileUri;

    public ImagesCaptureGalleryUpload() {
    }


    private static byte[] compressImage(String callingFunction, Context context, Uri fileUri, File photoFile
    ) {

        Bitmap thumb_bitmap = null;

        try {
            if (callingFunction.equals("imagePick")) {

                //compress image from Gallery
                thumb_bitmap = new Compressor(context)
                        .setMaxHeight(170)
                        .setMaxWidth(200)
                        .setQuality(50)
                        .compressToBitmap(new File(fileUri.getPath()));

            } else {
                thumb_bitmap = new Compressor(context)
                        .setMaxHeight(170)
                        .setMaxWidth(200)
                        .setQuality(50)
                        .compressToBitmap(photoFile);
            }
        } catch (IOException e) {

        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        final byte[] thumb_byte = baos.toByteArray();

        return thumb_byte;
    }


    public static void connectFirebaseUpload(String projectKey) {
        user = mAuth.getCurrentUser().getUid().toString();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mPrivateFullImageStorageRef =
                mStorage
                        .getReference(PRIVATE_FOLDER_CONTENTS_IMAGE_STORAGE_PATH)
                        .child(user);

        privateThumbNailsStorageRef =
                FirebaseStorage
                        .getInstance()
                        .getReference()
                        .child(PRIVATE_IMAGES_THUMBNAILS)
                        .child(user)
                        .child(projectKey);

        mPrivateFullImageDatabaseRef = FirebaseDatabase
                .getInstance()
                .getReference(PRIVATE_FOLDERS_CONTENTS)
                .child(user)
                .child(projectKey);
    }

    public static void attemptImageUpload(String callingFunction, File photoFile, Uri fileUri, Context context) {
        Toasty.info(context, "Uploading", Toast.LENGTH_SHORT).show();
        uploadThumbNail(callingFunction,context,fileUri,photoFile);
        uploadImage(callingFunction, fileUri, context);

    }

    private static void uploadThumbNail(String callingFunction,
                                 Context context, Uri fileUri, File photoFile) {

        UploadTask uploadTask;
        //upload the thumbnail to storage
        StorageReference thumb_filePath = privateThumbNailsStorageRef;

        uploadTask = thumb_filePath.putBytes(
                compressImage(callingFunction,context,fileUri,photoFile));

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
    }

    private static void uploadImage(String callingFunction, Uri fileUri, final Context context) {

        StorageReference filepath;
        Uri uri = null;

        if (callingFunction.equals("imagePick")) {
            uri = resultfileUri;
            filepath = mPrivateFullImageStorageRef.child(resultfileUri.getLastPathSegment());
        } else {

            filepath = mPrivateFullImageStorageRef
                    .child(fileUri.getLastPathSegment());
        }

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toasty.success(context, "Uploaded!", Toast.LENGTH_SHORT).show();

                @SuppressWarnings("VisibleForTests") String url = taskSnapshot.getDownloadUrl().toString();

                //save image info into the firebase database
                String uploadId = mPrivateFullImageDatabaseRef.push().getKey();

                NewImage imageUpload = new NewImage
                        ("",
                                url,
                                thumb_download_url,
                                GenericServices.date(),
                                uploadId);

                mPrivateFullImageDatabaseRef.child(uploadId).setValue(imageUpload);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toasty.error(
                        context,
                        "Uploading" +
                                "failed, please try again",
                        Toast.LENGTH_SHORT
                ).show();

            }
        });
    }


    //OTHER METHODS
    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    ImagesFragment.showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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




}
