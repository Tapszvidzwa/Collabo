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



 public class LoginActivity extends AppCompatActivity {

     private Button login;
     private EditText name;
     private EditText email;
     private Button takePhoto;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_login);

         login = (Button) findViewById(R.id.button);
         name = (EditText) findViewById(R.id.name);
         email = (EditText) findViewById(R.id.email);
         takePhoto = (Button) findViewById(R.id.takePhoto);


         takePhoto.setOnClickListener (new View.OnClickListener() {

             @Override
             public void onClick(View v) {
                 Intent cameraActivity = new Intent(LoginActivity.this, camera.class);
                 startActivity(cameraActivity);
             }
         });


         login.setOnClickListener(new View.OnClickListener() {


             @Override
             public void onClick(View view) {

                 String userName = "" + name.getText().toString();
                 String userEmail = "" + email.getText().toString();

                 if (userEmail == "" || userName == "") {
                     Toast.makeText(LoginActivity.this, "Enter the registration details", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 new LoginUser().execute(userName, userEmail);
             }
         });
     }

//jh
     //An asyncTask that will enable us to download some json content from the web
     private class LoginUser extends AsyncTask<String, Void, String> {


         @Override
         protected String doInBackground(String... params) {

             String postName = params[0];
             String postEmail = params[1];

             OkHttpClient client = new OkHttpClient();

             RequestBody formBody = new FormBody.Builder()
                     .add("name", postName)
                     .add("email", postEmail)
                     .build();

             Request request = new Request.Builder()
                     .url("http://132.161.226.52/test/processOrder.php")
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
             Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
         }
     }
 }