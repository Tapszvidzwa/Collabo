package com.example.tapiwa.collabo;


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

        FloatingActionButton saveNote = (FloatingActionButton) findViewById(R.id.saveNote);
        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
    }
    
    
    public void saveNote() {

        if(noteTitle.getText().toString().isEmpty()) {
           noteTitle.setError("Please enter unique note title");
            return;
        }


        //insert note into database
        dbHelper.insertNote(noteTitle.getText().toString(), noteContents.getText().toString());
        Toast.makeText(NewNote.this, noteTitle.getText().toString() + " has been saved", Toast.LENGTH_SHORT).show();
        NewNote.this.finish();
    }
}
