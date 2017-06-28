package com.example.tapiwa.collabo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


    public class camera extends AppCompatActivity {

        final int PHOTO_INTENT_SIMPLE = 1000;
        final int PHOTO_INTENT_WITH_FILENAME = 1001;
        final int VIDEO_INTENT_SIMPLE = 1002;

        Uri _photoFileUri;
        Button takePhoto;
        ImageView capturedPhoto;

        @Override

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_camera);

            takePhoto = (Button) findViewById(R.id.capture);
            capturedPhoto = (ImageView) findViewById(R.id.capturedImage);

          //  takePhoto.setOnClickListener(new View.OnClickListener() {
        //       @Override
        //        public void onClick(View v) {
                    _photoFileUri = generateTimeStampPhotoFileUri();
                    if(_photoFileUri != null) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, _photoFileUri);
                        startActivityForResult(intent, PHOTO_INTENT_WITH_FILENAME);
                    }
                }
       //     });
     //   }

        @Override
        protected void onActivityResult (int requestCode, int resultCode, Intent resultIntent) {
            Bundle extras = null;
            Bitmap imageBitmap = null;

            Log.d("Log_D", String.format("requestCode: %d | resultCode: %d", requestCode,resultCode));

            if(resultCode == RESULT_CANCELED ) {
                Toast.makeText(this, "user Cancelled", Toast.LENGTH_LONG).show();
                return;
            }

            switch(requestCode) {
                case PHOTO_INTENT_SIMPLE:

                    extras = resultIntent.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    break;

                case PHOTO_INTENT_WITH_FILENAME:
                    imageBitmap = BitmapFactory.decodeFile(_photoFileUri.getPath());
                    break;
            }

            if (imageBitmap != null) {
                capturedPhoto.setImageBitmap(imageBitmap);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            }
        }

            File getPhotoDirectory() {
                File outputDir = null;
                String externalStorageState = Environment.getExternalStorageState();

                if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
                    File pictureDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    outputDir = new File(pictureDir, "Pluralsight");
                    if (!outputDir.exists()) {
                        if (!outputDir.mkdirs()) {
                            Toast.makeText(this, "failed to create the output directory " + outputDir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                            outputDir = null;
                        }
                    }
                }
                return outputDir;
            }


            Uri generateTimeStampPhotoFileUri() {
                Uri photoFileUri = null;
                File outputDir = getPhotoDirectory();
                if(outputDir != null) {
                    String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
                    String photoFileName = "IMG_" + timeStamp + ".jpg";
                    File photoFile = new File (outputDir, photoFileName);
                    photoFileUri = Uri.fromFile(photoFile);
                }
                return photoFileUri;
            }

    }


















