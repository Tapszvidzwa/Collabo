package com.example.tapiwa.collegebuddy.classContents.notes;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.classContents.ClassContentsMainActivity;
import com.example.tapiwa.collegebuddy.miscellaneous.GenericServices;
import com.example.tapiwa.collegebuddy.R;

import es.dmoral.toasty.Toasty;


public class NewNote extends AppCompatActivity {
    
    private NotesSQLiteDBHelper dbHelper;
    private EditText noteTitle;
    private EditText noteContents;
    private Toolbar mToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        
        noteTitle = (EditText) findViewById(R.id.noteTitle);
        mToolBar = (Toolbar) findViewById(R.id.create_new_note_toolbar);
        mToolBar.setTitle("Create New Note");
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        noteContents = (EditText) findViewById(R.id.editNewNote);

        dbHelper = new NotesSQLiteDBHelper(this);

       // FloatingActionButton saveNote = (FloatingActionButton) findViewById(R.id.saveNote);
     /*   saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        }); */
    }
    
    
    public void saveNote() {

        if(noteTitle.getText().toString().trim().equals("")) {
           noteTitle.setError("Please enter unique note title");
            return;
        }



        String time = GenericServices.date();
        //insert note into database
        dbHelper.insertNote(ClassContentsMainActivity.className,
                noteTitle.getText().toString(),
                noteContents.getText().toString(),
                GenericServices.date());

        Toasty.success(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

        NewNote.this.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_note_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Main/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.cancel_note_creation) {
            finish();
        }

        if (id == R.id.save_new_note_icon) {
            saveNote();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
