package com.example.tapiwa.collabo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayNote extends AppCompatActivity {


    private TextView displayNoteTitle;
    private TextView displayNoteContents;
    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);

        displayNoteTitle = (TextView) findViewById(R.id.displayNoteTitle);
        displayNoteContents = (TextView) findViewById(R.id.displayNoteContent);

        String noteTitle = getIntent().getStringExtra("title");

        dbHelper = new DBHelper(this);

        displayNoteContents.setText(dbHelper.getNoteContents(noteTitle));
        displayNoteTitle.setText(noteTitle);
    }

}
