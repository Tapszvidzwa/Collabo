package com.example.tapiwa.collabo;

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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.zip.Inflater;


public class PrivatesFragment extends Fragment {

    public PrivatesFragment() {
    }

    public ListView foldersListView;
    private Vibrator vibrate;
    public ArrayList<NewProjectFolder> list;
    public PrivateTagListAdapter adapter;
    private DatabaseReference mDatabaseRef, mFolderContentsDBRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private ProgressDialog mProgress;
    private StorageReference storageReference;
    private String image_tag;
    final int REQUEST_IMAGE_CAPTURE = 1;
    public final static String USER_PRIVATE_LIST_OF_GROUPS = "List_Of_Private_User_Folders";
    private String user;
    private  File photoFile = null;
    private String thumb_download_url = null;
    private FloatingActionButton openNewFolder;
    Uri fileUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View privates = inflater.inflate(R.layout.fragment_privates, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid().toString();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        mProgress = new ProgressDialog(getContext());
        vibrate = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        mFolderContentsDBRef = FirebaseDatabase.getInstance().getReference(PrivateFolderContentsActivity.PRIVATE_FOLDERS_CONTENTS).child(user);

        foldersListView = (ListView) privates.findViewById(R.id.privates_folders_listV);
       // openNewFolder = (FloatingActionButton) privates.findViewById(R.id.private_create_new_folder);

       // openNewFolder = (FloatingActionButton) this.getActivity().findViewById(R.id.fragment_action);
      //  openNewFolder.setImageResource(R.drawable.ic_create_new_folder_white_24dp);
       // Main.actionButton.setBackgroundResource(R.drawable.ic_create_new_folder_white_24dp);
       // openNewFolder.setBackgroundResource(R.drawable.ic_create_new_folder_white_24dp);



        list = new ArrayList<>();
        adapter = new PrivateTagListAdapter(getContext(), R.layout.privates_folders_item_list, list);
        foldersListView.setAdapter(adapter);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(USER_PRIVATE_LIST_OF_GROUPS).child(user);
        mDatabaseRef.keepSynced(true);





setHasOptionsMenu(true);
        registerForContextMenu(foldersListView);



        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                list.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    NewProjectFolder projectNames = Snapshot1.getValue(NewProjectFolder.class);
                    list.add(projectNames);
                }
                Collections.reverse(list);


                adapter = new PrivateTagListAdapter(getContext(), R.layout.privates_folders_item_list, list);
                foldersListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });






        foldersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                NewProjectFolder item = (NewProjectFolder) parent.getItemAtPosition(position);


                Intent intent = new Intent(getContext(), PrivateFolderContentsActivity.class);
                intent.putExtra("projectKey", item.getProjectKey());

                //Start details activity
                startActivity(intent);
            }
        });

/*
        foldersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           int position, long id) {

                final ImageUpload item = (ImageUpload) parent.getItemAtPosition(position);
                vibrate.vibrate(40);

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }
                builder.setTitle("Delete Private Image")
                        .setMessage("Are you sure you want to delete this Image?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                //// TODO: 7/11/17 Please refactor this long code and also try to implement it in FirebaseHelper
                                String imageUri = item.getFull_image_uri();

                                DatabaseReference ref = mDatabaseRef;

                                ref.keepSynced(true);

                                final StorageReference photoRef = FirebaseStorage.getInstance().getReference(PrivateFolderContentsActivity);


                                final Query ImagesQuery = mDatabaseRef.orderByChild("full_image_uri").equalTo(imageUri);


                                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Image successfully deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(getContext(), "Image failed to delete" + exception, Toast.LENGTH_LONG).show();

                                    }
                                });

                                ImagesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                                            Snapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(getContext(), "Failed to delete Image", Toast.LENGTH_SHORT).show();
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
                return true;
            }
        }); */


    /*    Main.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getNewFolderName();
            }
        }); */
        return privates;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.private_folder_menu, menu);

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {


        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

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


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(("Enter new Name"));

        int maxLength = 50;
        final EditText newName = new EditText(getContext());
        newName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        newName.setInputType(InputType.TYPE_CLASS_TEXT);
        newName.setTextColor(Color.BLACK);
        newName.setVisibility(View.VISIBLE);
        builder.setView(newName);

        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(newName.getText().toString().length() > 0) {

                    NewProjectFolder projectFolder = (NewProjectFolder) adapter.getItem(position);

                    String projectKey = projectFolder.getProjectKey();

                    mDatabaseRef.child(projectKey).child("projectName").setValue(newName.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(!task.isSuccessful()) {
                                Toast toast = Toast.makeText(getContext(), "Failed to rename folder, try again", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                            adapter.notifyDataSetChanged();
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Please provide a new Course/Project name", Toast.LENGTH_SHORT).show();

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


        NewProjectFolder clickedFolder = (NewProjectFolder) adapter.getItem(position);
        final String clickedFolderProjectKey= clickedFolder.getProjectKey();



        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
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
                                                Toast.makeText(getContext(), "Folder deleted", Toast.LENGTH_SHORT).show();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(("Enter new Course/Project title"));

        int maxLength = 50;
        final EditText givenTitle = new EditText(getContext());
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
                    NewProjectFolder newProjectFolder = new NewProjectFolder(givenTitle.getText().toString(), projectKey);
                    mDatabaseRef.child(projectKey).setValue(newProjectFolder).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()) {
                                Toast toast = Toast.makeText(getContext(), "New folder created successfully", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(getContext(), "Failed to create folder, try again", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Please provide a Course/Project name", Toast.LENGTH_SHORT).show();

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
        NewProjectFolder newProjectFolder = new NewProjectFolder(givenTitle, projectKey);
        mDatabaseRef.child(projectKey).setValue(newProjectFolder).addOnCompleteListener(new OnCompleteListener<Void>() {
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