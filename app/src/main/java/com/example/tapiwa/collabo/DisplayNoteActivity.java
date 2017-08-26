package com.example.tapiwa.collabo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayNoteActivity extends AppCompatActivity {


    private TextView displayNoteTitle;
    private Toolbar mToolBar;
    private TextView displayNoteContents;
    private FloatingActionButton editNote;
    NotesSQLiteDBHelper dbHelper;
    private String opened_note_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);


        mToolBar = (Toolbar) findViewById(R.id.displayNote_toolbar);
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displayNoteTitle = (TextView) findViewById(R.id.displayNoteTitle);
        displayNoteContents = (EditText) findViewById(R.id.displayNoteContent);
        editNote = (FloatingActionButton) findViewById(R.id.EditNote);

        final String noteTitle = getIntent().getStringExtra("title");
        opened_note_title = noteTitle;

        dbHelper = new NotesSQLiteDBHelper(this);

        displayNoteContents.setText(dbHelper.getNoteContents(noteTitle));
        displayNoteTitle.setText(noteTitle);
        mToolBar.setTitle("Personal Notes");


        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayNoteContents.setCursorVisible(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(displayNoteContents, InputMethodManager.SHOW_FORCED);
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Main/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        //// TODO: 8/1/17 Change these settings to custom settings
        if (id == R.id.delete_note_icon) {
            
            //// TODO: 8/10/17 display are you sure you want to delete here
                dbHelper.deleteNote(opened_note_title);
            Toast.makeText(getApplicationContext(), "Deleted Note", Toast.LENGTH_SHORT).show();
                DisplayNoteActivity.this.finish();
        }

        if (id == R.id.save_note_icon) {
            dbHelper.updateNote(opened_note_title, displayNoteContents.getText().toString(), Collabos.getTime(), "default");
            Toast.makeText(getApplicationContext(), "New note updated", Toast.LENGTH_SHORT).show();
            DisplayNoteActivity.this.finish();
        }

        if(id == R.id.copy_note_icon) {
           copynote();
        }

        return super.onOptionsItemSelected(item);
    }


    private void copynote() {

        String text = displayNoteTitle.getText().toString() + "\n\n" + displayNoteContents.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copied text", text);
        clipboard.setPrimaryClip(clip);

        Toast toast = Toast.makeText(DisplayNoteActivity.this, "Copied note", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();




    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
