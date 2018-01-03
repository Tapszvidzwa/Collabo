package com.example.tapiwa.collegebuddy.Main.Vocabulary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tapiwa.collegebuddy.Main.Inbox.DisplayInboxNoteActivity;
import com.example.tapiwa.collegebuddy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.tapiwa.collegebuddy.Main.Vocabulary.DictionaryFragment.vocabDbRef;

public class WordsListActivity extends AppCompatActivity {



    private ListView WordsList;
    private ArrayList<VocabDatum> list;
    private WordsListAdapter wordsListAdapter;
    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.words_list);


        WordsList = (ListView) findViewById(R.id.worlds_listV);

        toolbar = (Toolbar) findViewById(R.id.words_list_toolbar);
        toolbar.setTitle("Vocabulary List");
        list = new ArrayList<>();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        wordsListAdapter = new WordsListAdapter(getApplicationContext(), R.layout.word_list_item, list);

        grabWordsFromFirebase();

        WordsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openNote = new Intent(WordsListActivity.this, DisplayInboxNoteActivity.class);

                openNote.putExtra("title", list.get(position).getWord());
                openNote.putExtra("noteContents", "Definition:\n\n" + list.get(position).getMeaning());

                startActivity(openNote);
            }

        });

    }


    private void grabWordsFromFirebase() {

        vocabDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                list.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    VocabDatum vocabDatum = Snapshot1.getValue(VocabDatum.class);
                    list.add(vocabDatum);
                }
                Collections.reverse(list);


                wordsListAdapter = new WordsListAdapter(getApplicationContext(), R.layout.word_list_item, list);
                WordsList.setAdapter(wordsListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
