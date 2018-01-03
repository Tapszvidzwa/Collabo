package com.example.tapiwa.collegebuddy.Main.ClassContents.Notes;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.StackView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.Main.ClassContents.ClassContentsMain.ClassContentsMainActivity;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class StackCards extends AppCompatActivity {


    public ArrayList<NoteStackItem> listCards;
    private NoteStackViewAdapter stackViewAdapter;
    private StackView stackView;
    private Toolbar mToolbar;
    private ArrayList<NoteStackItem> noteCards;
    private boolean codeMode = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_stack_cards);


        initializeViews();
       // firebaseInitialization();
        initializeListeners();

        //Connect to Facebook analytics
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

    }

    private void initializeViews() {

        listCards = new ArrayList<>();
        stackView = (StackView) findViewById(R.id.swipeCardsStack);
        mToolbar = (Toolbar) findViewById(R.id.swipeCardsToolbar);
        mToolbar.setTitle(ClassContentsMainActivity.className + " Notes");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        noteCards = (ArrayList<NoteStackItem>) args.getSerializable("ARRAYLIST");

        stackViewAdapter = new NoteStackViewAdapter(noteCards, R.layout.stack_note_item, StackCards.this, false);
        stackView.setAdapter(stackViewAdapter);
    }

    private void initializeListeners() {

                stackView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoteStackItem noteStackItem = noteCards.get(position);
                String title = noteStackItem.getTitle();
                Intent openDisplayNote = new Intent(StackCards.this, DisplayNoteActivity.class);
                openDisplayNote.putExtra("title", title);
                startActivity(openDisplayNote);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stack_cards_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Main/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //// TODO: 8/1/17 Change these settings to custom settings
        if (id == R.id.stack_cards_new_note_mode) {
            if(!codeMode) {
                codeMode = true;
                stackViewAdapter = new NoteStackViewAdapter(noteCards, R.layout.stack_note_item, StackCards.this, codeMode);
                Toasty.info(getApplicationContext(), getString(R.string.code_mode_activate), Toast.LENGTH_SHORT).show();

            } else {
                codeMode = false;
                stackViewAdapter = new NoteStackViewAdapter(noteCards, R.layout.stack_note_item, StackCards.this, codeMode);
                Toasty.info(getApplicationContext(), getString(R.string.code_mode_deactivate), Toast.LENGTH_SHORT).show();
            }


            stackView.setAdapter(stackViewAdapter);
            stackViewAdapter.notifyDataSetChanged();

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
            AppUsageAnalytics.incrementPageVisitCount("Stack_Cards");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
