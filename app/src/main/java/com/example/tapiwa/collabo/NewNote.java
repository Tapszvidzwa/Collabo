package com.example.tapiwa.collabo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewNote extends AppCompatActivity {
    
    private DBHelper dbHelper;
    private EditText noteTitle;
    private EditText noteContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        
        noteTitle = (EditText) findViewById(R.id.noteTitle);
        noteContents = (EditText) findViewById(R.id.editNewNote); 
        
        dbHelper = new DBHelper(this);

        FloatingActionButton saveNote = (FloatingActionButton) findViewById(R.id.addNote);
        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
    }
    
    
    public void saveNote() {
        
        //insert note into database
        dbHelper.insertNote(noteTitle.getText().toString(), noteContents.getText().toString());
        Toast.makeText(NewNote.this, noteTitle.getText().toString() + " has been saved", Toast.LENGTH_SHORT).show();

        Intent goBackToMain = new Intent(NewNote.this, Main.class);

        goBackToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        goBackToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        goBackToMain.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        startActivity(goBackToMain);
    }
}
