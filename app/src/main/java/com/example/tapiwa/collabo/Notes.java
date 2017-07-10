package com.example.tapiwa.collabo;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.tapiwa.collabo.R.layout.notes;

public class Notes extends Fragment {

    public ListView listview;
    public ArrayList<String> list;
    public NotesListAdapter adapter;

    public Notes() {
    }



    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View notesView = inflater.inflate(notes, container, false);

        listview = (ListView) notesView.findViewById(R.id.notesListView);
        list = new ArrayList<>();

        populateScreen();

        FloatingActionButton newNote = (FloatingActionButton) notesView.findViewById(R.id.addNote);
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
                Intent intent = new Intent(getContext(), DisplayNote.class);
                intent.putExtra("title", note);

                //Start details activity
                startActivity(intent);
            }
        });



        return notesView;

    }

    public void populateScreen() {
        dbHelper = new DBHelper(getContext());
        list = dbHelper.getAllTitles();
        Collections.reverse(list);
        adapter = new NotesListAdapter(getContext(),R.layout.note_item_list, list);
        listview.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        populateScreen();
    }

    public void createNewNote() {
        Intent writeNote = new Intent(getContext(), NewNote.class);
        startActivity(writeNote);
    }
}


