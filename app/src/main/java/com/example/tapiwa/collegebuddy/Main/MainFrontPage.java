package com.example.tapiwa.collegebuddy.Main;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Main.Vocabulary.DictionaryFragment;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.Settings;
import com.example.tapiwa.collegebuddy.classContents.classContentsMain.ClassContentsMainActivity;
import com.example.tapiwa.collegebuddy.classContents.classContentsMain.classImagesActivity;
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

public class MainFrontPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ListView foldersListView;
    private String[] drawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;


    private Vibrator vibrate;
    public ArrayList<NewClass> list;
    public ClassesAdapter adapter;
    public static DatabaseReference mDatabaseRef, mFolderContentsDBRef, mUserSessionsDBRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private Toolbar mToolBar, mToolBarTwo;
    private ProgressDialog mProgress;
    private StorageReference storageReference;
    private String image_tag;
    final int REQUEST_IMAGE_CAPTURE = 1;
    public final static String USER_PRIVATE_LIST_OF_GROUPS = "List_Of_Private_User_Folders";
    private final String USER_NUMBER_OF_SESSIONS = "Number_Of_Login_Sessions";
    private String user;
    private File photoFile = null;
    private String thumb_download_url = null;
    private FloatingActionButton createNewClass;
    public static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_front_page2);

        toolbar = (Toolbar) findViewById(R.id.frnt_pg_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        final android.app.Fragment fragment = new HomePageFragment();

// There are multiple `FragmentManagers`, be sure to always use the right one!
        FragmentManager manager = getFragmentManager();

// This starts the `FragmentTransaction`.
        FragmentTransaction transaction = manager.beginTransaction();

// Now you can define what happens in this transactions
// You can add/replace/remove/hide or show as many Fragments
// as you want in one `FragmentTransaction`.
// This command specifically adds the Fragment to the placeholder we defined
// in the layout of the Activity
        transaction.replace(R.id.fragment_place_holder, fragment);

// This commits the `FragmentTransaction`.
// Only after you call this will any changes be made
        transaction.commit();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mUserSessionsDBRef = database.getReference(USER_NUMBER_OF_SESSIONS);

        mUserSessionsDBRef.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null) {
                    mUserSessionsDBRef.child(user).setValue(new UserSessions(0));
                } else {
                    UserSessions previousCount = dataSnapshot.getValue(UserSessions.class);
                    int newCount = previousCount.getNum_of_sessions() + 1;
                    mUserSessionsDBRef.child(user).setValue(new UserSessions(newCount));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        initializeViews();
     //   initializeFirebase();
 //       grabClassesFromFirebase();
  //      setOnCLickListeners();


     //   FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

    //    fab.setOnClickListener(new View.OnClickListener() {
     //       @Override
    //        public void onClick(View view) {
    //         getNewFolderName();
    //        }
   //     });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeViews() {

        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        foldersListView = (ListView) findViewById(R.id.privates_folders_lstV);
        list = new ArrayList<>();
        adapter = new ClassesAdapter(getApplicationContext(), R.layout.privates_folders_item_lst, list);
        foldersListView.setAdapter(adapter);
        registerForContextMenu(foldersListView);

    }



    private void initializeFirebase() {

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

    }

    public void renameFolder(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainFrontPage.this);
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
                            } else {
                                Toasty.success(getApplicationContext(),
                                        "Renamed",
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

                                                                final SweetAlertDialog sdg = new SweetAlertDialog(MainFrontPage.this, SweetAlertDialog.SUCCESS_TYPE);
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




    private void getNewFolderName() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainFrontPage.this);
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
                    NewClass newClass = new NewClass(givenTitle.getText().toString(), projectKey, "blue");
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle("Collabo");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        //// TODO: 8/1/17 Change these settings to custom settings
        if (id == R.id.action_settings) {
            Intent settings = new Intent(MainFrontPage.this, Settings.class);
            startActivity(settings);
        }

        if (id == R.id.send_feedback) {
            Intent feedback = new Intent(MainFrontPage.this, SendFeedBackActivity.class);
            startActivity(feedback);
        }

        if(id == R.id.class_front_page_info) {
           showFrontPageClassDialogueInformation();
        }

        if(id == R.id.sign_out) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            this.finish();
        }


        return super.onOptionsItemSelected(item);
    }

    private void showFrontPageClassDialogueInformation() {

        SweetAlertDialog sdg = new SweetAlertDialog(MainFrontPage.this, SweetAlertDialog.NORMAL_TYPE);
        sdg.setTitleText("Usage info");
        sdg.setContentText(getResources().getString(R.string.classes_page_information));
        sdg.setCancelable(true);
        sdg.show();

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.vocabulary) {
           android.app.Fragment fragment = new DictionaryFragment();

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_place_holder, fragment)
                    .commit();
        }

        if (id == R.id.home) {
            android.app.Fragment fragment = new HomePageFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_place_holder, fragment)
                    .commit();
        }

        if(id == R.id.calculator) {
            android.app.Fragment fragment = new Calculator();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_place_holder, fragment)
                    .commit();

        }

       /* if(id == R.id.GPAcalculator) {
            android.app.Fragment fragment = new GPACalculatorFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_place_holder, fragment)
                    .commit();

        } */

        if(id == R.id.converter) {
            android.app.Fragment fragment = new Conveter();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_place_holder, fragment)
                    .commit();

        }

        if(id == R.id.new_features) {
            android.app.Fragment fragment = new NewFeatures();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_place_holder, fragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
}
