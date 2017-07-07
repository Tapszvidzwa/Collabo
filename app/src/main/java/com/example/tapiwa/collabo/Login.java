package com.example.tapiwa.collabo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.wang.avi.AVLoadingIndicatorView;

import android.util.Log;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import static com.example.tapiwa.collabo.R.id.avi;
import static com.example.tapiwa.collabo.R.layout.activity_login;

public class  Login extends AppCompatActivity implements View.OnClickListener {
    private EditText password;
    private EditText email;
    private final String TAG = "FB_SIGNIN";

    private final int MIN_SESSION_DURATION = 3000;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAnalytics mFBAnalytics;
    public AVLoadingIndicatorView spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_login);

        spinner = (AVLoadingIndicatorView) findViewById(R.id.avi);

        //FirebaseAnalytics setup
        mFBAnalytics = FirebaseAnalytics.getInstance(this);
        mFBAnalytics.setMinimumSessionDuration(MIN_SESSION_DURATION);


        //setOnClickListeners to the buttons
        findViewById(R.id.accessAccountBtn).setOnClickListener(this);
        findViewById(R.id.registrationActivityBtn).setOnClickListener(this);

        password = (EditText) findViewById(R.id.loginPassword);
        email = (EditText) findViewById(R.id.loginEmail);


        //Get a reference to the Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // TODO: Attach a new AuthListener to detect sign in and out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //user signed in
                    openCollaboHome();
                } else {
                    // User is signed out

                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void showLoadingSpinner() {
        spinner.setVisibility(View.VISIBLE);
    }

    public void stopLoadingSpinner() {
        spinner.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accessAccountBtn:
                attemptLogin();
                break;

            case R.id.registrationActivityBtn:
                openRegistrationActivity();
                break;
        }
    }

    public boolean checkLoginFieldsCompleted() {
        String userEmail, userPassword;

        userEmail = email.getText().toString();
        userPassword = password.getText().toString();

        if (userEmail.isEmpty()) {
            email.setError("Please enter your email");
            return false;
        }
        if (userPassword.isEmpty()) {
            password.setError("Please enter your password");
            return false;
        }

        return true;
    }

    private void openRegistrationActivity() {
        Intent openRegistrationAct = new Intent(Login.this, RegistrationActivity.class);
        startActivity(openRegistrationAct);
    }


    private void openCollaboHome() {
        Intent openCollaboActivity = new Intent(Login.this, Main.class);

        openCollaboActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openCollaboActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        openCollaboActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        startActivity(openCollaboActivity);
    }

    public void hideKeyboard() {
        //Hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
    }



    private void attemptLogin() {

        hideKeyboard();

        if (!checkLoginFieldsCompleted())
            return;


        showLoadingSpinner();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    showLoadingSpinner();
                                    openCollaboHome();
                                } else {
                                    stopLoadingSpinner();
                                    Toast.makeText(Login.this, "Sign in failed", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            stopLoadingSpinner();
                            Toast.makeText(Login.this, "You entered invalid credentials", Toast.LENGTH_SHORT)
                                    .show();
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            stopLoadingSpinner();
                            Toast.makeText(Login.this, "No pref_account is associated with that email", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            stopLoadingSpinner();
                            Toast.makeText(Login.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }


}





