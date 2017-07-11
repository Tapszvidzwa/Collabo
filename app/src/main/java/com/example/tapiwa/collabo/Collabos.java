package com.example.tapiwa.collabo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.support.constraint.R.id.parent;


public class Collabos extends Fragment {

    public Collabos() {
    }

    public GridView gridView;
    public ArrayList<ImageUpload> list;
    public ImageListAdapter adapter;
    private DatabaseReference mDatabaseRef;
    final String FB_DATABASE_PATH = "photos";
    Vibrator vibrate;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View collabos = inflater.inflate(R.layout.collabos, container, false);
        gridView = (GridView) collabos.findViewById(R.id.gridview);
        list = new ArrayList<>();
        adapter = new ImageListAdapter(getContext(), R.layout.image_item_list, list);
        gridView.setAdapter(adapter);
   vibrate = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);


        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        mDatabaseRef.keepSynced(true);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                list.clear();
                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    ImageUpload img = Snapshot1.getValue(ImageUpload.class);
                    list.add(img);
                }
                Collections.reverse(list);


                //init adapter
                adapter = new ImageListAdapter(getContext(), R.layout.image_item_list, list);
                gridView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                ImageUpload item = (ImageUpload) parent.getItemAtPosition(position);
                //Pass the image title and url to DetailsActivity
                Intent intent = new Intent(getContext(), MaximizeImage.class);
                intent.putExtra("title", item.getTag());
                intent.putExtra("image", item.getUrl());
                intent.putExtra("name", item.getProfileName());

                //Start details activity
                startActivity(intent);
            }
        });



        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {




            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           int position, long id) {

             final  ImageUpload item = (ImageUpload) parent.getItemAtPosition(position);
                vibrate.vibrate(40);

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }
                builder.setTitle("Delete Collabo")
                        .setMessage("Are you sure you want to delete this Collabo?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                //// TODO: 7/11/17 Please refactor this long code and also try to implement it in FirebaseHelper
                                String imageUri = item.getUrl();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.keepSynced(true);
                                final StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri);
                                final Query ImagesQuery = ref.child(FB_DATABASE_PATH).orderByChild("url").equalTo(imageUri);


                                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Collabo successfully deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(getContext(), "Collabo failed to delete" + exception, Toast.LENGTH_LONG).show();

                                    }
                                });

                                ImagesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
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
        });



        return collabos;
    }





}


