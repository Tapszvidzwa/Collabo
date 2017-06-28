package com.example.tapiwa.collabo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.tapiwa.collabo.R.layout.activity_login;

public class login extends AppCompatActivity {


    private Button accessAccount;
    private Button linkToRegistration;
    private EditText password;
    private EditText email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_login);

        accessAccount = (Button) findViewById(R.id.accessAccountBtn);
        linkToRegistration = (Button) findViewById(R.id.linkToRegistrationActivity);
        password = (EditText) findViewById(R.id.loginPassword);
        email = (EditText) findViewById(R.id.loginEmail);

        linkToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openRegistrationActivity = new Intent(login.this, registrationActivity.class);
                startActivity(openRegistrationActivity);
                finish();
            }
        });

        /*If successful, this logs in user to the account*/
        // TODO: 4/29/17 Aunthenticate user login properly
        accessAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  if (validateRegistrationDetails()) {
                    String userPassword = password.getText().toString().trim();
                    String userEmail = email.getText().toString().trim();
                    new loginUser().execute(userPassword, userEmail);
                } */

               openPhotoActivity();
            }
        });
    }


    Boolean validateRegistrationDetails() {
        Boolean isValid = true;

         /* if nothing has been entered on the email, give an error */
        if(email.getText().toString().trim().equals("")) {
            email.setError("Please enter your email address");
            isValid = false;
        }

         /* if nothing has been entered on password, give an error */
        if(password.getText().toString().trim().equals("")) {
            password.setError("Please enter your password");
            isValid = false;
        }
        return isValid;
    }


    void openPhotoActivity() {
        Intent photosActivity = new Intent(login.this, camera.class);
        startActivity(photosActivity);
    }


    /* An asyncTask that will enable us to download some json response from the WebServer */
    private class loginUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String postPassword = params[0];
            String postEmail = params[1];

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("password", postPassword)
                    .add("email", postEmail)
                    .build();

            Request request = new Request.Builder()
                    .url("http://132.161.226.52/test/loginUser.php")
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject response = new JSONObject(s);
                boolean error = response.getBoolean("error");

                if (!error) {
                    //user succefully logged in
                    //open photoActivity
                    openPhotoActivity();
                } else {
                    Toast.makeText(login.this, "Invalid login details", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
