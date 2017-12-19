package com.example.tapiwa.collegebuddy.authentication;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Main.MainFrontPage;
import com.example.tapiwa.collegebuddy.Main.NewClass;
import com.example.tapiwa.collegebuddy.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;


import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

import static com.facebook.FacebookSdk.getApplicationContext;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private EditText password;
    private EditText email;
    private CallbackManager callbackManager;
    private LoginButton facebookloginButton;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static DatabaseReference mDatabaseRef, permissionsRef;
    private static final String PERMISSIONS = "User_Permissions";
    private TextView forgotPassword;

    private GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 0;
    private static String TAG = "MAIN_ACTIVITY";

    private final int MIN_SESSION_DURATION = 3000;

    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFBAnalytics;


    public AVLoadingIndicatorView spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //




        spinner = (AVLoadingIndicatorView) findViewById(R.id.avi);

        //FirebaseAnalytics setup
        mFBAnalytics = FirebaseAnalytics.getInstance(this);
        mFBAnalytics.setMinimumSessionDuration(MIN_SESSION_DURATION);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(RegistrationActivity.FB_USERS_PATH);
        connectPermissions();




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "user is logged in ");
                    updateUI(user);
                } else {
                    Log.d(TAG, "user is logged out");
                    updateUI(null);
                }

            }
        };


        facebookloginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        facebookloginButton.setReadPermissions("email", "public_profile");

        facebookloginButton.setText("Sign in with Facebook");


        //setOnClickListeners to the buttons
        findViewById(R.id.accessAccountBtn).setOnClickListener(this);
        findViewById(R.id.registrationActivityBtn).setOnClickListener(this);
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);


        callbackManager = CallbackManager.Factory.create();

        password = (EditText) findViewById(R.id.loginPassword);
        email = (EditText) findViewById(R.id.loginEmail);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordToEmail();
            }
        });


        //Get a reference to the Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        facebookloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {


                //Get profile details
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {
                                    String userfbname = object.getString("name");
                                    handleFacebookAccessToken(loginResult.getAccessToken(), userfbname);
                                } catch (Exception e) {

                                    Toasty.error(LoginActivity.this,
                                            "There was a problem logging in",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });

                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginActivity.this, "Error to Login Facebook", Toast.LENGTH_SHORT).show();
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


    }

    public static void connectPermissions() {
        permissionsRef = FirebaseDatabase.getInstance().getReference(PERMISSIONS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            //it must be facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
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

            case R.id.google_sign_in_button:
                googleSignin();
                break;
        }
    }


    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d("AUTH", "user logged in: " + user.getEmail());
            openLoadingMain();
        } else {
            Log.d("AUTH", "user logged out");
            //   Toast.makeText(Login.this, "Login failed, sign up if you are a new member", Toast.LENGTH_LONG).show();
        }
    }

    public void showLoadingSpinner() {
        spinner.setVisibility(View.VISIBLE);
    }

    public void stopLoadingSpinner() {
        spinner.setVisibility(View.INVISIBLE);
    }

    private void sendPasswordToEmail() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setIcon(R.drawable.ic_keyboard_black_24px);
        builder.setTitle(("Enter your email address"));

        int maxLength = 100;
        final EditText email = new EditText(getApplicationContext());
        email.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        email.setInputType(InputType.TYPE_CLASS_TEXT);
        email.setTextColor(Color.BLACK);
        email.setVisibility(View.VISIBLE);
        builder.setView(email);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = email.getText().toString() ;

                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(getApplicationContext(), "Email Sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toasty.success(getApplicationContext(), "Email not found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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
        Intent openRegistrationAct = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(openRegistrationAct);
    }

    private void openLoadingMain() {
        Intent openCollaboActivity = new Intent(LoginActivity.this, MainFrontPage.class);

        openCollaboActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openCollaboActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        openCollaboActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        startActivity(openCollaboActivity);
    }

    public void hideKeyboard() {
        //Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                                    openLoadingMain();
                                } else {
                                    stopLoadingSpinner();
                                    Toasty.error(LoginActivity.this,
                                            "Sign in failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            stopLoadingSpinner();

                            Toasty.error(LoginActivity.this,
                                    "You entered invalid credentials",
                                    Toast.LENGTH_SHORT).show();

                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            stopLoadingSpinner();

                            Toasty.error(LoginActivity.this,
                                    "No pref_account is associated with that email",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            stopLoadingSpinner();
                            Toasty.error(LoginActivity.this,
                                    e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*============================ FACEBOOK SIGN IN STUFF =====================> */

    private void handleFacebookAccessToken(final AccessToken token, final String name) {

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();

                            //create and add new user
                            String bio = "Hi there, I have joined Collabo";
                            String image_uri = "default";
                            String thumb_image = "default";
                            NewUser newUser = new NewUser(name, uid, bio, image_uri, thumb_image);
                            mDatabaseRef.child(uid).setValue(newUser);

                            updateUI(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*<=========================== GOOGLE SIGN IN STUFF =======================> */

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "Google handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            final String fullName = acct.getDisplayName().toString();


            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                Log.d(TAG, "Google login credentials: success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();

                                //create and add new user
                                String bio = "Hi there, I have joined Collabo";
                                String image_uri = "default";
                                String thumb_image = "default";
                                NewUser newUser = new NewUser(fullName, uid, bio, image_uri, thumb_image);
                                mDatabaseRef.child(uid).setValue(newUser);

                                updateUI(user);
                            } else {
                                Log.d(TAG, "Google login sign in credentials failure");
                                updateUI(null);
                            }

                        }
                    });
        } else {
            Log.d("AUTH", "Google unable to authenticate google credentials");
            // Signed out, show unauthenticated UI.
            updateUI(null);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Google sign in failed");
    }

    private void googleSignin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}





