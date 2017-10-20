package com.example.tapiwa.collegebuddy.classContents.notes;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.StackView;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.R;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;
import java.util.Collections;


public class StackCards extends AppCompatActivity {


    public ArrayList<NoteStackItem> listCards;
    private NoteStackViewAdapter stackViewAdapter;
    private StackView stackView;
    private Toolbar mToolbar;
    private ArrayList<NoteStackItem> noteCards;



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
        mToolbar.setTitle("Cards");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        noteCards = (ArrayList<NoteStackItem>) args.getSerializable("ARRAYLIST");

        //swipeStackAdapter = new SwipeStackAdapter(listCards, StackCardsImages.this);
       // stackView.setAdapter(swipeStackAdapter);

    }

    private void initializeListeners() {

               // Collections.reverse(noteCards);
                stackViewAdapter = new NoteStackViewAdapter(noteCards, R.layout.stack_note_item, StackCards.this);
                stackView.setAdapter(stackViewAdapter);


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
