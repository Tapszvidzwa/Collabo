package com.example.tapiwa.collegebuddy.authentication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;


import com.example.tapiwa.collegebuddy.Main.MainFrontPage;
import com.example.tapiwa.collegebuddy.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import static com.example.tapiwa.collegebuddy.authentication.LoginActivity.permissionsRef;

public class Tutorial extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {
        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest

        /*this one works*/
        addSlide(AppIntroFragment.newInstance("Adding classes",
                "Create new class folders to store your images, notes and documents" +
                        " by pressing the plus button on the bottom right ",
                R.drawable.add_class_new, R.color.intro_first));

        addSlide(AppIntroFragment.newInstance("Capture and Upload Images",
                "Use the Camera button to capture and upload images",
                R.drawable.save_image_new, R.color.intro_second));

        addSlide(AppIntroFragment.newInstance("Choose from Gallery",
                "You can alternatively choose an existing image from the gallery" +
                        " to upload and add to your collection",
                R.drawable.save_image_gallery_new, R.color.intro_third));

       /*this one works*/
       addSlide(AppIntroFragment.newInstance("Create new note",
                "Use the add note button to start creating a new note",
                R.drawable.save_note, R.color.intro_fourth));


    /*this one works*/
        addSlide(AppIntroFragment.newInstance("Save notes",
                "Complete creating a note by clicking the save button",
                R.drawable.add_new_note_new, R.color.intro_fifth));

         /*this one works*/
        addSlide(AppIntroFragment.newInstance("Search notes and images",
                "Use the search button to search for your class notes or images",
                R.drawable.search_image_notes, R.color.intro_sixth));
//////////////////////////////////////////////////////////////////////////////////////////////
        // OPTIONAL METHODS
        // Override bar/separator color
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button
        showSkipButton(true);
        showDoneButton(true);

        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permesssion in Manifest
        setVibrate(true);
        setVibrateIntensity(30);

    }


    @Override
    public void onSkipPressed() {
    //Do something when users tap on Skip button.
        Intent openFrontPage = new Intent(this, MainFrontPage.class);
        startActivity(openFrontPage);
        this.finish();
    }

    @Override
    public void onDonePressed() {
    //Do something when users tap on Done button.
        Intent openFrontPage = new Intent(this, MainFrontPage.class);
        startActivity(openFrontPage);
        this.finish();
    }

    @Override
    public void onNextPressed() {
    }

    @Override
    public void onSlideChanged() {
    }

}
