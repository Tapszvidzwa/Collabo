package com.example.tapiwa.collabo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.preference.PreferenceManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class Main extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private ProgressDialog mProgress;
    private String image_tag;
    private AVLoadingIndicatorView loadingSpinner;
    public GridView gridView;
    SharedPreferences usrName;
    public ArrayList<ImageUpload> list;
    public ImageListAdapter adapter;
    private DatabaseReference mDatabaseRef;
    final String FB_DATABASE_PATH = "photos";
    Uri fileUri;


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        image_tag = "";
        usrName = PreferenceManager.getDefaultSharedPreferences(this);

        mProgress = new ProgressDialog(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton takePhoto = (FloatingActionButton) findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TakePicture();
            }
        });

    }



    private void TakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

                fileUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    public void startUpload() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(("Provide the image Tag"));

        int maxLength = 40;
        final EditText tag = new EditText(this);
        tag.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
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
                Toast.makeText(Main.this, "Upload cancelled, no tag", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            startUpload();
        }
    }

    public void attemptImageUpload() {
        mProgress.setMessage("Uploading Collabo...");
        mProgress.show();
        //Upload the picture to the Photo folder in the Storage bucket
        //// TODO: 6/29/17 change the uri so that its custom for every photo



        StorageReference filepath = storageReference.child("Photo").child(fileUri.getLastPathSegment());

        filepath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress.dismiss();
                Toast.makeText(Main.this, "Uploading finished", Toast.LENGTH_SHORT).show();

                String userName = usrName.getString("example_text", null);
                ImageUpload imageUpload = new ImageUpload("@" + userName, image_tag, taskSnapshot.getDownloadUrl().toString());

                //save image info into the firebase database
                String uploadId = mDatabaseRef.push().getKey();
                mDatabaseRef.child(uploadId).setValue(imageUpload);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgress.dismiss();
                Toast.makeText(Main.this, "Uploading failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Main/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(Main.this, Settings.class);
            startActivity(settings);
        }

        return super.onOptionsItemSelected(item);
    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0 :
                    Collabos collabos = new Collabos();
                    return collabos;
                case 1 :
                    Tags tags = new Tags();
                    return tags;
                case 2: Notes notes = new Notes();
                    return notes;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "COLLABOS";
                case 1:
                    return "TAGS";
                case 2:
                    return "NOTES";
            }
            return null;
        }
    }
}
