package com.example.tapiwa.collegebuddy.Main.NewFeatures;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.classContents.classContentsMain.ClassContentsMainActivity;
import com.example.tapiwa.collegebuddy.classContents.notes.NotesSQLiteDBHelper;
import com.example.tapiwa.collegebuddy.miscellaneous.GenericServices;

import java.util.ArrayList;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

import static com.example.tapiwa.collegebuddy.Main.NewFeatures.NewFeaturesFragment.featuresDbRef;


public class AddFeature extends AppCompatActivity {

    private EditText featureTitle;
    private EditText featureContents;
    private Toolbar mToolBar;
    private FloatingActionButton send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feature);
        
        featureTitle = (EditText) findViewById(R.id.feature_title);
        mToolBar = (Toolbar) findViewById(R.id.create_new_feature_toolbar);
        send = (FloatingActionButton) findViewById(R.id.send_feature);
        mToolBar.setTitle("Create New Feature");
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        featureContents = (EditText) findViewById(R.id.feature_description_txt);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewFeature nf = new NewFeature(
                        featureContents.getText().toString(), featureTitle.getText().toString());

                String pushKey = featuresDbRef.push().getKey();
                featuresDbRef.child(pushKey).setValue(nf);
                AddFeature.this.finish();

            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
