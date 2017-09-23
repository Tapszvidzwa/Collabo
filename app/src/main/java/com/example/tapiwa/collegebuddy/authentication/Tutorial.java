package com.example.tapiwa.collegebuddy.authentication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.tapiwa.collegebuddy.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class Tutorial extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance("Adding classes",
                "Create new class folders to store your images, notes and documents" +
                "by pressing the plus button on the bottom right ",
                R.drawable.add_class, Color.BLUE));
//
        // OPTIONAL METHODS
        // Override bar/separator color
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button
        showSkipButton(false);
        showDoneButton(false);

        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permesssion in Manifest
        setVibrate(true);
        setVibrateIntensity(30);

    }


    @Override
    public void onSkipPressed() {
// Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed() {
// Do something when users tap on Done button.
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onSlideChanged() {
    }



}
