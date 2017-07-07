package com.example.tapiwa.collabo;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.Collections;



public class FirebaseHelper {



    DatabaseReference db;
    ArrayList<ImageUpload> list = new ArrayList<>();

    public FirebaseHelper(DatabaseReference db) {
        this.db = db;
    }


    //READ
    public ArrayList<ImageUpload> retrieve()
    {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return list;
    }


    private void fetchData(DataSnapshot dataSnapshot)
    {
        list.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            ImageUpload tag = ds.getValue(ImageUpload.class);
            list.add(tag);
        }

        Collections.reverse(list);
    }
}