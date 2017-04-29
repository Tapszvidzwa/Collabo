 package com.example.tapiwa.collabo;

 import android.content.Intent;
 import android.os.AsyncTask;
 import android.support.v7.app.AppCompatActivity;
 import android.os.Bundle;
 import android.view.View;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.Toast;
 import java.io.IOException;
 import okhttp3.OkHttpClient;
 import okhttp3.Request;
 import okhttp3.Response;
 import okhttp3.RequestBody;
 import okhttp3.FormBody;


 public class registrationActivity extends AppCompatActivity {

     private Button registerBtn;
     private EditText password;
     private EditText email;
     private Button loginBtn;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_registration);

         registerBtn = (Button) findViewById(R.id.button);
         password = (EditText) findViewById(R.id.password);
         email = (EditText) findViewById(R.id.email);
         loginBtn = (Button) findViewById(R.id.login);


         loginBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent openLogin = new Intent(registrationActivity.this, login.class);
                 startActivity(openLogin);
             }
             });


         registerBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 /* Register the user if they have provided valid registration details */
                 if(validateRegistrationDetails()) {
                     String userPassword = password.getText().toString().trim();
                     String userEmail = email.getText().toString().trim();
                     new registerUser().execute(userPassword, userEmail);
                 }
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


     /* An asyncTask that will enable us to download some json response from the WebServer */
     private class registerUser extends AsyncTask<String, Void, String> {

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
                     .url("http://132.161.226.52/test/registerUser.php")
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
             Toast.makeText(registrationActivity.this, s, Toast.LENGTH_SHORT).show();
         }
     }
 }