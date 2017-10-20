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

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Main.MainFrontPage;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.classContents.classContentsMain.ClassContentsMainActivity;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by tapiwa on 10/19/17.
 */

public class CameraGalleryUpload extends AppCompatActivity {


    public static FirebaseAuth mAuth;
    public static final String PRIVATE_FOLDERS_CONTENTS = "Private_Folders_Contents";
    public static final String PRIVATE_FOLDER_CONTENTS_IMAGE_STORAGE_PATH = "Private_Folders_Photos";
    public static String PRIVATE_IMAGES_THUMBNAILS = "Private_Images_Thumbnails";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1002;
    private static final int PICK_IMAGE = 1001;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public static FirebaseStorage mStorage;
    public static StorageReference mPrivateFullImageStorageRef;
    public static StorageReference privateThumbNailsStorageRef;
    public static DatabaseReference mPrivateFullImageDatabaseRef;
    public static boolean galleryImage;

    public static File photoFile;
    public static Uri fileUri;

    public static String image_tag, thumb_download_url, user, projectKey;

    public CameraGalleryUpload() {
    }

    public CameraGalleryUpload(String projectKey) {
        this.projectKey = projectKey;
    }


    private static byte[] compressImage(Context context, Uri fileUri, File photoFile
    ) {

        Bitmap thumb_bitmap = null;

        //if image is from gallery obtain the bytes first, then compress
        if(galleryImage) {

            //setup the image stream and obtain the bytes
            InputStream imageStream = null;
            try {
                imageStream = context.getContentResolver().openInputStream(
                        fileUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            thumb_bitmap = BitmapFactory.decodeStream(imageStream);

        } else {

            //else image is from camera, thus compress it straight away
            try {
                //compress image from Gallery
                thumb_bitmap = new Compressor(context)
                        .setMaxHeight(170)
                        .setMaxWidth(200)
                        .setQuality(50)
                        .compressToBitmap(photoFile);

            } catch (IOException e) {

            }
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        final byte[] thumb_byte = baos.toByteArray();

        return thumb_byte;
    }


    public static void connectFirebaseCloud() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid().toString();
        mStorage = FirebaseStorage.getInstance();

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

    public static void attemptImageUpload(File photoFile, Uri fileUri, Context context) {

        Toasty.info(context, "Uploading", Toast.LENGTH_SHORT).show();
        uploadThumbNail(context,fileUri,photoFile);
        uploadImage(fileUri, context);

    }

    private static void uploadThumbNail(Context context, Uri fileUri, File photoFile) {

        UploadTask uploadTask;
        //upload the thumbnail to storage
        StorageReference thumb_filePath = privateThumbNailsStorageRef;

        uploadTask = thumb_filePath.putBytes(
                compressImage(context,fileUri,photoFile));

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

    private static void uploadImage(Uri fileUri, final Context context) {

        StorageReference filepath;

        filepath = mPrivateFullImageStorageRef.child(fileUri.getLastPathSegment());

        filepath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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

    public static void chooseImageFromGallery(Activity activity) {


        AppUsageAnalytics.incrementPageVisitCount("Gallery_Images");

        if (checkPermissionREAD_EXTERNAL_STORAGE(activity)) {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");
            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
            activity.startActivityForResult(chooserIntent, PICK_IMAGE);
        } else {
            Toast.makeText(activity, "You need access to media Gallery to choose images", Toast.LENGTH_SHORT).show();
        }
    }

    public static void takePicture(Activity activity, String callingActivity) {


            AppUsageAnalytics.incrementPageVisitCount("Photos_Taken");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            try {
                photoFile = createImageFile(activity);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,
                        "com.example.android.fileprovider",
                        photoFile);

                fileUri = photoURI;

                if(callingActivity.equals("ClassContentsMainActivity")) {
                    ClassContentsMainActivity.photoFile = photoFile;
                    ClassContentsMainActivity.resultfileUri = fileUri;
                } else if(callingActivity.equals("MainFrontPage")) {
                    MainFrontPage.photoFile = photoFile;
                    MainFrontPage.resultFileUri = fileUri;
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
                    Toast.makeText(getApplicationContext(), "Denied Access",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    public static boolean checkPermissionREAD_EXTERNAL_STORAGE(
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

    public static void showDialog(final String msg, final Context context,
                           final String permission) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setIcon(R.drawable.ic_perm_media_black_24px);
        alertBuilder.setTitle("Access to Gallery Permission");
        alertBuilder.setMessage("Permission is necessary to select an image");
        alertBuilder.setPositiveButton("Give permission",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
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

    public static void deleteImage(String imageKey, String fullImageUri, String thumbUri, Activity activity) {

        final SweetAlertDialog sdg = new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE);
        sdg.setTitleText("Deleted");
        sdg.setConfirmText("");
        sdg.show();
        //get the database reference for image
        //delete the from the database
        DatabaseReference fullImageRef = mPrivateFullImageDatabaseRef.child(imageKey).getRef();
        fullImageRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Thread myThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(1200);
                            sdg.dismiss();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                myThread.start();
            }
        });
    }


}
