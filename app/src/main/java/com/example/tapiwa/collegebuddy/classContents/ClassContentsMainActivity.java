package com.example.tapiwa.collegebuddy.classContents;


        import android.Manifest;
        import android.app.Activity;
        import android.app.ProgressDialog;
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
        import android.os.Vibrator;
        import android.support.annotation.NonNull;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.TabLayout;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v4.view.MenuItemCompat;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.SearchView;
        import android.support.v7.widget.Toolbar;


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
        import android.widget.EditText;
        import android.widget.Toast;

        import com.example.tapiwa.collegebuddy.R;
        import com.example.tapiwa.collegebuddy.classContents.images.ImagesFragment;
        import com.example.tapiwa.collegebuddy.classContents.images.NewImage;
        import com.example.tapiwa.collegebuddy.classContents.notes.NotesListAdapter;
        import com.example.tapiwa.collegebuddy.miscellaneous.GenericServices;
        import com.example.tapiwa.collegebuddy.classContents.notes.NewNote;
        import com.example.tapiwa.collegebuddy.classContents.notes.NotesFragment;
        import com.facebook.FacebookSdk;
        import com.facebook.appevents.AppEventsLogger;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.iid.FirebaseInstanceId;
        import com.google.firebase.messaging.FirebaseMessaging;
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

        import static com.example.tapiwa.collegebuddy.classContents.notes.NotesFragment.dbHelper;
        import static com.example.tapiwa.collegebuddy.classContents.notes.NotesFragment.list;
        import static com.example.tapiwa.collegebuddy.classContents.notes.NotesFragment.listview;
        import static com.example.tapiwa.collegebuddy.classContents.notes.NotesFragment.notesAdapter;
        import static com.facebook.FacebookSdk.getApplicationContext;


public class ClassContentsMainActivity extends AppCompatActivity implements NotesFragment.SearchNotesInterface {

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    private FirebaseUser mCurrentUser;
    public static String uid, className, projectKey;
    public static FloatingActionButton actionButton;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    public static StorageReference mPrivateFullImageStorageRef;
    public static StorageReference privateThumbNailsStorageRef;
    public static DatabaseReference mPrivateFullImageDatabaseRef;

    private Menu mMenu;
    private SearchView searchView;
    private FirebaseStorage mStorage;

    public static final String PRIVATE_FOLDERS_CONTENTS = "Private_Folders_Contents";
    public static String PRIVATE_IMAGES_THUMBNAILS = "Private_Images_Thumbnails";
    public static final String PRIVATE_FOLDER_CONTENTS_IMAGE_STORAGE_PATH = "Private_Folders_Photos";
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1002;
    private final int PICK_IMAGE = 1001;
    private final int REQUEST_IMAGE_CAPTURE = 1;

    private ProgressDialog mProgress;
    private String image_tag, thumb_download_url, user;
    private File photoFile = null;
    private Uri resultfileUri;
    File thumb_file_path;
    private Vibrator vibrate;
    private int pageNumber = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_contents_main_activity);

        className = getIntent().getStringExtra("projectName");
        projectKey = getIntent().getStringExtra("projectKey");

        initializeViews();
        firebaseInitialization();
        initializeListeners();

        //Connect to Facebook analytics
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //Subscribe to topic and get token from firebase
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();

    }

    private void initializeViews() {

        //toolbar setup
        Toolbar toolbar = (Toolbar) findViewById(R.id.class_contents_toolbar);
        toolbar.setTitle(className);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //viewpager setup
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mViewPagerAdapter);

        //floatingBtn
        actionButton = (FloatingActionButton) findViewById(R.id.fragment_action);
        actionButton.setImageResource(R.drawable.ic_perm_media_white_24px);
        actionButton.show();

        //tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);

        vibrate = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
        mProgress = new ProgressDialog(getApplicationContext());

    }

    private void initializeListeners() {

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    //privates fragment
                    actionButton.setImageResource(R.drawable.ic_perm_media_white_24px);
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chooseImageFromGallery();
                        }
                    });
                    actionButton.show();

                    pageNumber = 0;
                }

                if (position == 1) {
                    //my notes fragment
                    actionButton.setImageResource(R.drawable.ic_note_add_white_24px);
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent writeNote = new Intent(getApplicationContext(), NewNote.class);
                            startActivity(writeNote);
                        }
                    });
                    actionButton.show();
                    pageNumber = 1;
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void firebaseInitialization() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        user = mAuth.getCurrentUser().getUid().toString();
        mStorage = FirebaseStorage.getInstance();

        mPrivateFullImageStorageRef = mStorage
                .getReference(PRIVATE_FOLDER_CONTENTS_IMAGE_STORAGE_PATH)
                .child(user);
        privateThumbNailsStorageRef = FirebaseStorage
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

        mPrivateFullImageDatabaseRef.keepSynced(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //   resultfileUri = data.getData();
            startUpload("imageCapture");
        }


        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            try {
                photoFile = createImageFile();
                resultfileUri = data.getData();
                thumb_file_path = new File(resultfileUri.toString());
                startUpload("imagePick");
            } catch (Exception e) {
            }
        }
    }

    @Override
    public  void searchNote(String noteTitle) {
        list.clear();

        if(noteTitle.equals("")) {
         list = dbHelper.getAllTitles(className);
            Collections.reverse(list);
        } else {
            list = dbHelper.searchNote(noteTitle);
        }

        notesAdapter = new NotesListAdapter(getApplicationContext(), R.layout.note_item_list, list, className);
        listview.setAdapter(notesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.class_contents_menu, menu);


        MenuItem searchItem = menu.findItem(R.id.class_contents_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

           @Override
            public boolean onQueryTextChange(String query) {
               searchNote(query);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        ///////////
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
      /*  if (id == R.id.class_contents_search) {
        } */

        if (id == R.id.class_contents_info) {
            showInfomation(pageNumber);
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInfomation(int pageNumber) {
        if (pageNumber == 0) {
            imagesPageDialogueInformation();
        } else if (pageNumber == 1) {
            notesPageDialogueInformation();
        } else
            return;
    }

    private void imagesPageDialogueInformation() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ClassContentsMainActivity.this);
        alertBuilder.setCancelable(true);
        alertBuilder.setIcon(R.drawable.ic_help_outline_black_24px);
        alertBuilder.setTitle("Usage Info");
        alertBuilder.setMessage(R.string.images_page_information);
        alertBuilder.setCancelable(true);
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void notesPageDialogueInformation() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ClassContentsMainActivity.this);
        alertBuilder.setCancelable(true);
        alertBuilder.setIcon(R.drawable.ic_help_outline_black_24px);
        alertBuilder.setTitle("Usage Info");
        alertBuilder.setMessage(R.string.notes_page_information);
        alertBuilder.setCancelable(true);
        AlertDialog alert = alertBuilder.create();
        alert.show();
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

    public void chooseImageFromGallery() {

        if (checkPermissionREAD_EXTERNAL_STORAGE(ClassContentsMainActivity.this)) {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");
            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
            startActivityForResult(chooserIntent, PICK_IMAGE);
        } else {
            Toast.makeText(ClassContentsMainActivity.this, "You need access to media Gallery to choose images", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ClassContentsMainActivity.this, "Denied Access",
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

    public void showDialog(final String msg, final Context context,
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

    public void startUpload(final String callingFunction) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ClassContentsMainActivity.this);
        builder.setTitle(("Enter the image title"));
        builder.setIcon(R.drawable.ic_keyboard_black_24px);

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

    private void attemptImageUpload(String callingFunction) {

        Toast.makeText(getApplicationContext(),
                "Uploading... Image will be added once done",
                Toast.LENGTH_SHORT)
                .show();

        //Upload the picture to the Photo folder in the Storage bucket
        //// TODO: 6/29/17 change the uri so that its custom for every photo
        try {
            //upload the thumb_uri

            Bitmap thumb_bitmap;

            if (callingFunction.equals("imagePick")) {

                InputStream imageStream = null;
                try {
                    imageStream = getApplicationContext().getContentResolver().openInputStream(
                            resultfileUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                thumb_bitmap = BitmapFactory.decodeStream(imageStream);

            } else {

                thumb_bitmap = new Compressor(getApplicationContext())
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .setQuality(60)
                        .compressToBitmap(ImagesFragment.photoFile);
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


        StorageReference filepath;
        Uri uri;

        if (callingFunction.equals("imagePick")) {
            uri = resultfileUri;
            filepath = mPrivateFullImageStorageRef.child(resultfileUri.getLastPathSegment());
        } else {
            uri = ImagesFragment.fileUri;
            filepath = mPrivateFullImageStorageRef
                    .child(ImagesFragment.fileUri.getLastPathSegment());
        }

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(getApplicationContext(),
                        "Uploading finished",
                        Toast.LENGTH_SHORT)
                        .show();

                @SuppressWarnings("VisibleForTests") String url = taskSnapshot.getDownloadUrl().toString();

                //save image info into the firebase database
                String uploadId = mPrivateFullImageDatabaseRef.push().getKey();

                NewImage imageUpload = new NewImage
                        (image_tag,
                                url,
                                thumb_download_url,
                                GenericServices.date(),
                                uploadId);

                mPrivateFullImageDatabaseRef.child(uploadId).setValue(imageUpload);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgress.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Uploading failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        searchView.onActionViewCollapsed();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0 :
                    // PrivatesFragment privatesFragment = new PrivatesFragment();
                    ImagesFragment imagesFragment = new ImagesFragment();
                    return imagesFragment;
                case 1:
                    NotesFragment notesFragment = new NotesFragment();
                    return notesFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "IMAGES";
                case 1:
                    return "NOTES";
            }
            return null;
        }
    }

}
