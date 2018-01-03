package com.example.tapiwa.collegebuddy.Main.Class;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.R;
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


public class ClassesFragment extends AppCompatActivity {

    public ListView foldersListView;
    private ViewPager mViewPager;
    private Vibrator vibrate;
    public ArrayList<NewClass> list;
    public ClassesAdapter adapter;
    private DatabaseReference mDatabaseRef, mFolderContentsDBRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
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
              //  foldersListView.setAdapter(notesAdapter);
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
                Intent intent = new Intent(getApplicationContext(), classImagesActivity.class);

                intent.putExtra("projectKey", item.getProjectKey());
                intent.putExtra("projectName", item.getProjectName());

                //Start details activity
                startActivity(intent);
            }
        });

        createNewClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        mDatabaseRef = FirebaseDatabase.getInstance()
                .getReference(USER_PRIVATE_LIST_OF_GROUPS)
                .child(user);
        mDatabaseRef
                .keepSynced(true);
    }

    private void initializeViews() {
        createNewClass = (FloatingActionButton) findViewById(R.id.add_class);
        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        foldersListView = (ListView) findViewById(R.id.privates_folders_listV);
        list = new ArrayList<>();
        adapter = new ClassesAdapter(getApplicationContext(), R.layout.privates_folders_item_lst, list);
//        foldersListView.setAdapter(notesAdapter);
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
                                Toast toast = Toast.makeText(getApplicationContext(), "Failed to rename folder, try again", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                            adapter.notifyDataSetChanged();
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Please provide a new Course/Project name", Toast.LENGTH_SHORT).show();

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



        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getApplicationContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getApplicationContext());
        }


        builder.setTitle("Delete Folder")
                .setMessage("Are you sure you want to delete this folder? This will delete all its contents.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

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
                                                Toast.makeText(getApplicationContext(), "Folder deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
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


    private void getNewFolderName() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setIcon(R.drawable.ic_keyboard_black_24dp);
        builder.setTitle(("Enter new Course/Project title"));

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
                                Toast toast = Toast.makeText(getApplicationContext(), "New folder created successfully", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Failed to create folder, try again", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Please provide a Course/Project name", Toast.LENGTH_SHORT).show();

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
        NewClass newClass = new NewClass(givenTitle, projectKey, "blue");
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