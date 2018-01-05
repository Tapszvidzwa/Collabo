package com.example.tapiwa.collegebuddy.Main.HomePage;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Main.Class.ClassesAdapter;
import com.example.tapiwa.collegebuddy.Main.Class.NewClass;
import com.example.tapiwa.collegebuddy.Main.Class.ClassFolderColor;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.Main.ClassContents.ClassContentsMain.ClassContentsMainActivity;
import com.example.tapiwa.collegebuddy.Main.ClassContents.ClassContentsMain.classImagesActivity;
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

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by tapiwa on 10/5/17.
 */

public class HomePageFragment extends Fragment {


    public ListView foldersListView;
    private String[] drawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;


    private Vibrator vibrate;
    public ArrayList<NewClass> list;
    public ClassesAdapter adapter;
    private DatabaseReference mDatabaseRef, mFolderContentsDBRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    public static ImageView inbxNotification;
    private Toolbar mToolBar, mToolBarTwo;
    private ProgressDialog mProgress;
    private StorageReference storageReference;
    private String image_tag;
    final int REQUEST_IMAGE_CAPTURE = 1;
    public final static String USER_PRIVATE_LIST_OF_GROUPS = "List_Of_Private_User_Folders";
    public final static String NUMBER_OF_IMAGES = "Number_Of_Images_In_Folder";
    public final static String NUMBER_OF_DOCUMENTS = "Number_Of_Documents_In_Folder";
    public final static String NUMBER_OF_NOTES = "Number_Of_Notes_In_Folder";
    public final static String NUMBER_OF_DEADLINES = "Number_Of_Deadlines_In_Folder";
    private String user;
    private File photoFile = null;
    private String thumb_download_url = null;
    private FloatingActionButton createNewClass;
    private int selectedFolder = 0;

View homePageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homePageView = inflater.inflate(R.layout.fragment_homepage, container, false);

        initializeViews();
        initializeFirebase();
        grabClassesFromFirebase();
        setOnCLickListeners();

           FloatingActionButton fab = (FloatingActionButton) homePageView.findViewById(R.id.addFolder);

            fab.setOnClickListener(new View.OnClickListener() {
               @Override
                public void onClick(View view) {
                 getNewFolderName();
                }
             });

        return homePageView;


    }

    public void renameFolder(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        AppUsageAnalytics.incrementPageVisitCount("Deleted_Folders");

        NewClass clickedFolder = (NewClass) adapter.getItem(position);
        final String clickedFolderProjectKey= clickedFolder.getProjectKey();

        final SweetAlertDialog dg = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);

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

                                                                final SweetAlertDialog sdg = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
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


    private void initializeViews() {

        vibrate = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        foldersListView = (ListView) homePageView.findViewById(R.id.privates_folders_lstV);
        MainFrontPageActivity.toolbar.setTitle("Collabo");
        list = new ArrayList<>();
        adapter = new ClassesAdapter(getApplicationContext(), R.layout.privates_folders_item_lst, list);
        foldersListView.setAdapter(adapter);
        registerForContextMenu(foldersListView);
        inbxNotification = (ImageView) homePageView.findViewById(R.id.inbx_notification);

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
                mDatabaseRef.keepSynced(true);


                adapter = new ClassesAdapter(getApplicationContext(), R.layout.privates_folders_item_lst, list);
                foldersListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void chooseFolderColor() {


        final ClassFolderColor[] items = {
                new ClassFolderColor("Blue", R.drawable.ic_collabofolder),
                new ClassFolderColor("Yellow", R.drawable.ic_collabofolderyellow),
                new ClassFolderColor("Red", R.drawable.ic_collabofolderred),
                new ClassFolderColor("Green", R.drawable.ic_collabofoldergreen),
                new ClassFolderColor("Dark Pink", R.drawable.ic_collabofoldernewdarkpink),
                new ClassFolderColor("Orange", R.drawable.ic_collabofolderneworange),
                new ClassFolderColor("Purple", R.drawable.ic_collabofoldernewpurple)
        };

        ListAdapter adapter = new ArrayAdapter<ClassFolderColor>(
                getActivity(),
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items){

            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);

                return v;
            }
        };


        new AlertDialog.Builder(getActivity())
                .setTitle("Choose Color")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        switch (item) {
                            case 0:
                                updateNewColor("blue", selectedFolder);
                                break;
                            case 1:
                                updateNewColor("yellow", selectedFolder);
                                break;
                            case 2:
                                updateNewColor("red", selectedFolder);
                                break;
                            case 3:
                                updateNewColor("green", selectedFolder);
                                break;
                            case 4:
                                updateNewColor("darkPink", selectedFolder);
                                break;
                            case 5:
                                updateNewColor("orange", selectedFolder);
                                break;
                            case 6:
                                updateNewColor("purple", selectedFolder);
                                break;

                        }

                        //...
                    }
                }).show();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.private_folder_menu, menu);
    }

    private void updateNewColor(String color, int position) {

        NewClass selectedClass = list.get(position);

        mDatabaseRef
                .child(selectedClass.getProjectKey())
                .child("folderColor")
                .setValue(color);

        AppUsageAnalytics.incrementPageVisitCount("Change_Folder_Color");
        //add on complete listener

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        selectedFolder = info.position;

        switch(item.getItemId()){
            case R.id.delete_folder:
                continueWithDeleteFolder(info.position);
                return true;
            case R.id.rename_folder:
                renameFolder(info.position);
                return true;
            case R.id.change_folder_color:
                   chooseFolderColor();
            default:
                return super.onContextItemSelected(item);
        }
    }


    private void getNewFolderName() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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


}
