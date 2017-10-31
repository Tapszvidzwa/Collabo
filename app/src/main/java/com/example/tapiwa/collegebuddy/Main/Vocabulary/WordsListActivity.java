package com.example.tapiwa.collegebuddy.Main.Vocabulary;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.eightbitlab.bottomnavigationbar.BottomBarItem;
import com.eightbitlab.bottomnavigationbar.BottomNavigationBar;
import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Main.Calculator;
import com.example.tapiwa.collegebuddy.Main.ChooseClass;
import com.example.tapiwa.collegebuddy.Main.ClassesAdapter;
import com.example.tapiwa.collegebuddy.Main.Goals.GoalsFragment;
import com.example.tapiwa.collegebuddy.Main.HomePageFragment;
import com.example.tapiwa.collegebuddy.Main.Inbox.DisplayInboxNoteActivity;
import com.example.tapiwa.collegebuddy.Main.Inbox.InboxFragment;
import com.example.tapiwa.collegebuddy.Main.NewClass;
import com.example.tapiwa.collegebuddy.Main.NewFeatures.NewFeaturesFragment;
import com.example.tapiwa.collegebuddy.Main.UserProfileFragment;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.Settings;
import com.example.tapiwa.collegebuddy.authentication.LoginActivity;
import com.example.tapiwa.collegebuddy.classContents.classContentsMain.classImagesActivity;
import com.example.tapiwa.collegebuddy.classContents.images.CameraGalleryUpload;
import com.example.tapiwa.collegebuddy.miscellaneous.GenericServices;
import com.example.tapiwa.collegebuddy.miscellaneous.SendFeedBackActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.example.tapiwa.collegebuddy.Main.Vocabulary.DictionaryFragment.vocabDbRef;
import static com.example.tapiwa.collegebuddy.authentication.LoginActivity.permissionsRef;
import static com.facebook.FacebookSdk.getApplicationContext;

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
