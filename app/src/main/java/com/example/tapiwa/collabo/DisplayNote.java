package com.example.tapiwa.collabo;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayNote extends AppCompatActivity {


    private TextView displayNoteTitle;
    private TextView displayNoteContents;
    private FloatingActionButton deleteNote;
    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);


        displayNoteTitle = (TextView) findViewById(R.id.displayNoteTitle);
        displayNoteContents = (TextView) findViewById(R.id.displayNoteContent);
        deleteNote = (FloatingActionButton) findViewById(R.id.deleteNote);

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
    }

}
