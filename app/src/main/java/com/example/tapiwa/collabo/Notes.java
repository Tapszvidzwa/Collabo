package com.example.tapiwa.collabo;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.tapiwa.collabo.R.id.container;
import static com.example.tapiwa.collabo.R.layout.notes;
import static java.security.AccessController.getContext;

public class Notes extends AppCompatActivity {

    public ListView listview;
    public ArrayList<String> list;
    public NotesListAdapter adapter;
    private Toolbar mToolbar;

    private DBHelper dbHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);

        mToolbar = (Toolbar) findViewById(R.id.list_of_notes);
        mToolbar.setTitle("Personal Notes");


        listview = (ListView) findViewById(R.id.notesListView);
        list = new ArrayList<>();
        populateScreen();
        FloatingActionButton newNote = (FloatingActionButton) findViewById(R.id.addNote);
        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewNote();
            }
        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                String note = (String) parent.getItemAtPosition(position);
               //  String contents = dbHelper.getNoteContents(note);

                //Pass the image title and url to DetailsActivity
                Intent intent = new Intent(getApplicationContext(), DisplayNote.class);
                intent.putExtra("title", note);

                //Start details activity
                startActivity(intent);
            }
        });

    }

    public void populateScreen() {
        dbHelper = new DBHelper(getApplicationContext());
        list = dbHelper.getAllTitles();
        Collections.reverse(list);
        adapter = new NotesListAdapter(getApplicationContext(),R.layout.note_item_list, list);
        listview.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        populateScreen();
    }

    public void createNewNote() {
        Intent writeNote = new Intent(getApplicationContext(), NewNote.class);
        startActivity(writeNote);
    }
}


