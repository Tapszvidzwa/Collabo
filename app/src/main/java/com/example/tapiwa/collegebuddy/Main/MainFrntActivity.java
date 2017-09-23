package com.example.tapiwa.collegebuddy.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.Settings;
import com.example.tapiwa.collegebuddy.authentication.LoginActivity;
import com.example.tapiwa.collegebuddy.authentication.WelcomeActivity;
import com.example.tapiwa.collegebuddy.classContents.ClassContentsMainActivity;
import com.example.tapiwa.collegebuddy.classContents.classImagesActivity;
import com.example.tapiwa.collegebuddy.miscellaneous.SendFeedBackActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class MainFrntActivity extends AppCompatActivity {

    public ListView foldersListView;
    private ViewPager mViewPager;
    private Vibrator vibrate;
    public ArrayList<NewClass> list;
    public ClassesAdapter adapter;
    private DatabaseReference mDatabaseRef, mFolderContentsDBRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private Toolbar mToolBar, mToolBarTwo;
    private ProgressDialog mProgress;
    private StorageReference storageReference;
    private String image_tag;
    final int REQUEST_IMAGE_CAPTURE = 1;
    public final static String USER_PRIVATE_LIST_OF_GROUPS = "List_Of_Private_User_Folders";
    private String user;
    private File photoFile = null;
    private String thumb_download_url = null;
    private FloatingActionButton createNewClass;
    Uri fileUri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);
        initializeViews();
        initializeFirebase();
        grabClassesFromFirebase();
        setOnCLickListeners();

    }


    private void grabClassesFromFirebase() {

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                list.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    NewClass projectNames = Snapshot1.getValue(NewClass.class);
                    list.add(projectNames);
                }
                Collections.reverse(list);


                adapter = new ClassesAdapter(getApplicationContext(), R.layout.privates_folders_item_lst, list);
                foldersListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void setOnCLickListeners() {

        foldersListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick (AdapterView < ? > parent, View v,int position, long id){

                //Get item at position
                NewClass item = (NewClass) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ClassContentsMainActivity.class);

                intent.putExtra("projectKey", item.getProjectKey());
                intent.putExtra("projectName", item.getProjectName());

                //Start details activity
                startActivity(intent);
            }
        });

        createNewClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            getNewFolderName();
            }
        });


    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid().toString();

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();

        mFolderContentsDBRef = FirebaseDatabase.getInstance()
                .getReference(classImagesActivity.PRIVATE_FOLDERS_CONTENTS)
                .child(user);
        mFolderContentsDBRef.keepSynced(true);

        mDatabaseRef = FirebaseDatabase.getInstance()
                .getReference(USER_PRIVATE_LIST_OF_GROUPS)
                .child(user);
        mDatabaseRef
                .keepSynced(true);
    }

    private void initializeViews() {
        mToolBarTwo = (Toolbar) findViewById(R.id.classesToolbarTwo);
        mToolBar = (Toolbar) findViewById(R.id.classesToolBar);
        createNewClass = (FloatingActionButton) findViewById(R.id.add_class);
        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        foldersListView = (ListView) findViewById(R.id.privates_folders_listV);
        list = new ArrayList<>();
        adapter = new ClassesAdapter(getApplicationContext(), R.layout.privates_folders_item_lst, list);
        foldersListView.setAdapter(adapter);

        mToolBarTwo.setTitle("Classes");
        mToolBar.setTitle("Collabo");
        setSupportActionBar(mToolBar);

        registerForContextMenu(foldersListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.private_folder_menu, menu);

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()){
            case R.id.delete_folder:
                continueWithDeleteFolder(info.position);
                return true;
            case R.id.rename_folder:
                renameFolder(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }



    public void renameFolder(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle(("Enter new Name"));
        builder.setIcon(R.drawable.ic_keyboard_black_24dp);

        int maxLength = 50;
        final EditText newName = new EditText(getApplicationContext());
        newName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        newName.setInputType(InputType.TYPE_CLASS_TEXT);
        newName.setTextColor(Color.BLACK);
        newName.setVisibility(View.VISIBLE);
        builder.setView(newName);

        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(newName.getText().toString().length() > 0) {

                    NewClass projectFolder = (NewClass) adapter.getItem(position);

                    String projectKey = projectFolder.getProjectKey();

                    mDatabaseRef.child(projectKey).child("projectName").setValue(newName.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(!task.isSuccessful()) {
                                Toasty.error(
                                        getApplicationContext(),
                                        "Failed to rename folder, please try again",
                                        Toast.LENGTH_SHORT).show();
                            }

                            adapter.notifyDataSetChanged();
                        }
                    });

                } else {
                    Toasty.warning
                            (
                                    getApplicationContext(),
                                    "Please provide a new Course/Project name",
                                    Toast.LENGTH_SHORT).show();

                }
                dialog.dismiss();
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


    public void continueWithDeleteFolder(int position) {


        NewClass clickedFolder = (NewClass) adapter.getItem(position);
        final String clickedFolderProjectKey= clickedFolder.getProjectKey();

        final SweetAlertDialog dg = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);

                dg.setTitleText("Are you sure?")
                .setContentText("You won't be able to recover this folder and its contents!")
                .setConfirmText("Yes,delete it!")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {



                        //remove folder from database
                        mDatabaseRef.child(clickedFolderProjectKey)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {

                                            mFolderContentsDBRef
                                                    .child(clickedFolderProjectKey)
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {

                                                                final SweetAlertDialog sdg = new SweetAlertDialog(MainFrntActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                                                sdg.setTitleText("Deleted").setConfirmText("").show();

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


                                                                //Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                        sweetAlertDialog.dismiss();
                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dg.dismissWithAnimation();
            }
        })
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
        if (id == R.id.action_settings) {
            Intent settings = new Intent(MainFrntActivity.this, Settings.class);
            startActivity(settings);
        }

        if (id == R.id.send_feedback) {
            Intent feedback = new Intent(MainFrntActivity.this, SendFeedBackActivity.class);
            startActivity(feedback);
        }

        if(id == R.id.class_front_page_info) {
            showFrontPageClassDialogueInformation();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFrontPageClassDialogueInformation() {

        SweetAlertDialog sdg = new SweetAlertDialog(MainFrntActivity.this, SweetAlertDialog.NORMAL_TYPE);
        sdg.setTitleText("Usage info");
        sdg.setContentText(getResources().getString(R.string.classes_page_information));
        sdg.setCancelable(true);
        sdg.show();

    }


    private void getNewFolderName() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainFrntActivity.this);
        builder.setIcon(R.drawable.ic_keyboard_black_24px);
        builder.setTitle(("Enter new class name"));

        int maxLength = 50;
        final EditText givenTitle = new EditText(getApplicationContext());
        givenTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        givenTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        givenTitle.setTextColor(Color.BLACK);
        givenTitle.setVisibility(View.VISIBLE);
        builder.setView(givenTitle);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(givenTitle.getText().toString().length() > 0) {
                    String projectKey = mDatabaseRef.push().getKey();
                    NewClass newClass = new NewClass(givenTitle.getText().toString(), projectKey);
                    mDatabaseRef.child(projectKey).setValue(newClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()) {
                                Toasty.success(getApplicationContext(), "Class created!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toasty.error(getApplicationContext(), "Failed to create class, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toasty.info(getApplicationContext(), "Please provide a Course/Project name", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
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


    public static void createNewFolder(String givenTitle, String path, final Context context) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user = mAuth.getCurrentUser().getUid().toString();

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(path).child(user);


        String projectKey = mDatabaseRef.push().getKey();
        NewClass newClass = new NewClass(givenTitle, projectKey);
        mDatabaseRef.child(projectKey).setValue(newClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    Toast toast = Toast.makeText(context, "New folder created successfully", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(context, "Failed to create folder, try again", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }

}