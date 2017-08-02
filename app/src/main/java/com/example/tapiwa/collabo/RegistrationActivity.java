package com.example.tapiwa.collabo;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText password, displayName;
    private EditText email;
    private Toolbar mToolBar;
    private final String TAG = "FB_REGISTER";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private AVLoadingIndicatorView spinner;
    public static final String FB_USERS_PATH = "Users";
    private SharedPreferences storedDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        findViewById(R.id.registerUser).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_USERS_PATH);
        storedDisplayName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        displayName = (EditText) findViewById(R.id.displaName);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();
        spinner = (AVLoadingIndicatorView) findViewById(R.id.avi);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.registerUser:
                attemptRegisterUser();
                break;

            case R.id.login:
                openLoginActivity();
                break;
        }
    }

    private void openLoginActivity() {
        Intent openLoginAct = new Intent(RegistrationActivity.this, Login.class);
        startActivity(openLoginAct);
    }


    public boolean checkLoginFieldsCompleted() {
        String userEmail, userPassword, userName;

        userEmail = email.getText().toString().trim();
        userName = displayName.getText().toString().trim();
        userPassword = password.getText().toString();

        if (userName.isEmpty()) {
            email.setError("Please enter your email");
            return false;
        }

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

    private void openCollaboHomeActivity() {
        Intent openCollaboAct = new Intent(RegistrationActivity.this, Main.class);
        startActivity(openCollaboAct);
    }

    public void hideKeyboard() {
        //Hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
    }

    public void setUserName(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(Tags.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor Editor = sharedPreferences.edit();
        Editor.putString("username",  "@" + name);
        Editor.commit();
    }


    private void showLoadingSpinner() {
        spinner.setVisibility(View.VISIBLE);
    }

    private void stopLoadingSpinner() {
        spinner.setVisibility(View.INVISIBLE);
    }



    private void attemptRegisterUser() {

        hideKeyboard();

        if (!checkLoginFieldsCompleted())
            return;

        showLoadingSpinner();

        String usrEmail = email.getText().toString().trim();
        String usrPassword = password.getText().toString();
        final String usrName = displayName.getText().toString().trim();

        //final String userName = usrName.toString();


        mAuth.createUserWithEmailAndPassword(usrEmail, usrPassword)
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    stopLoadingSpinner();
                                    Toast.makeText(RegistrationActivity.this, "Collabo Buddy successfully created", Toast.LENGTH_SHORT)
                                            .show();

                                    setUserName(usrName);

                                    String uid = mAuth.getCurrentUser().getUid();
                                 //   User newUser = new User(userName, uid);

                                    HashMap<String, String> map = new HashMap();
                                    map.put("name", usrName);
                                    map.put("uid", uid);
                                    map.put("bio", "Hi there, I have joined Collabo");
                                    map.put("image_uri", "default");
                                    map.put("thumb_image", "default");

                                  mDatabaseRef.child(uid).setValue(map);
                                   openCollaboHomeActivity();
                                    finish();

                                } else {
                                    stopLoadingSpinner();
                                    Toast.makeText(RegistrationActivity.this, "Account creation failed", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            stopLoadingSpinner();
                            Toast.makeText(RegistrationActivity.this, "That email address is already in use", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            stopLoadingSpinner();
                            Toast.makeText(RegistrationActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }
 }