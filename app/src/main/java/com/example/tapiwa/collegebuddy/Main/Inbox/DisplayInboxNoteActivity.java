package com.example.tapiwa.collegebuddy.Main.Inbox;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.classContents.classContentsMain.ClassContentsMainActivity;
import com.example.tapiwa.collegebuddy.classContents.notes.NotesSQLiteDBHelper;
import com.example.tapiwa.collegebuddy.miscellaneous.GenericServices;

import es.dmoral.toasty.Toasty;

public class DisplayInboxNoteActivity extends AppCompatActivity {


    private EditText displayNoteTitle;
    private Toolbar mToolBar;
    private EditText displayNoteContents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);

        mToolBar = (Toolbar) findViewById(R.id.displayNote_toolbar);
        mToolBar.setTitle("Note Card");
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displayNoteContents = (EditText) findViewById(R.id.displayNoteContent);
        displayNoteTitle = (EditText) findViewById(R.id.displayNoteTitle);

        final String noteTitle = getIntent().getStringExtra("title");
        final String noteContents = getIntent().getStringExtra("noteContents");

        displayNoteTitle.setText(noteTitle);
        displayNoteContents.setText(noteContents);

        Linkify.addLinks(displayNoteContents, Linkify.ALL);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notes_menu_inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Main/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.copy_note_icon) {
            copynote();
        }


        return super.onOptionsItemSelected(item);
    }


    private void copynote() {

        String text = displayNoteTitle.getText().toString() + "\n\n" + displayNoteContents.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copied text", text);
        clipboard.setPrimaryClip(clip);

        Toast toast = Toast.makeText(DisplayInboxNoteActivity.this, "Copied", Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppUsageAnalytics.incrementPageVisitCount("Display_Note");
    }

}
