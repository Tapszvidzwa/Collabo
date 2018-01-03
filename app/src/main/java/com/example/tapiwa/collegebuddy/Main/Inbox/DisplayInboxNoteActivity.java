package com.example.tapiwa.collegebuddy.Main.Inbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.R;

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

        return super.onOptionsItemSelected(item);
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
