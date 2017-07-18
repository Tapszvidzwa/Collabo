package com.example.tapiwa.collabo;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DisplayNote extends AppCompatActivity {


    private TextView displayNoteTitle;
    private TextView displayNoteContents;
    private FloatingActionButton deleteNote, saveNote, editNote;
    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);


        displayNoteTitle = (TextView) findViewById(R.id.displayNoteTitle);
        displayNoteContents = (EditText) findViewById(R.id.displayNoteContent);
        deleteNote = (FloatingActionButton) findViewById(R.id.deleteNote);
        saveNote = (FloatingActionButton) findViewById(R.id.saveEditedNote);
        editNote = (FloatingActionButton) findViewById(R.id.EditNote);

        final String noteTitle = getIntent().getStringExtra("title");

        dbHelper = new DBHelper(this);

        displayNoteContents.setText(dbHelper.getNoteContents(noteTitle));
        displayNoteTitle.setText(noteTitle);

        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbHelper.deleteNote(noteTitle);
                DisplayNote.this.finish();
            }
        });

        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbHelper.updateNote(noteTitle, displayNoteContents.getText().toString());
                DisplayNote.this.finish();
            }
        });

        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayNoteContents.setCursorVisible(true);
            }
        });
    }

}
