package com.example.tapiwa.collabo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.wang.avi.AVLoadingIndicatorView;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText password;
    private EditText email;
    private final String TAG = "FB_REGISTER";
    private FirebaseAuth mAuth;
    private AVLoadingIndicatorView spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        findViewById(R.id.registerUser).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);

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

    private void openCollaboHomeActivity() {
        Intent openCollaboAct = new Intent(RegistrationActivity.this, Main.class);
        startActivity(openCollaboAct);
    }

    private void hideKeyboard() {
        //Hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
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

        String usrEmail = email.getText().toString();
        String usrPassword = password.getText().toString();


        mAuth.createUserWithEmailAndPassword(usrEmail, usrPassword)
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    stopLoadingSpinner();
                                    Toast.makeText(RegistrationActivity.this, "Collabo Buddy successfully created", Toast.LENGTH_SHORT)
                                            .show();
                                   openCollaboHomeActivity();
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