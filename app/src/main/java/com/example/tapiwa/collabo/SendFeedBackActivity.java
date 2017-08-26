package com.example.tapiwa.collabo;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendFeedBackActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private Button mSendBtn;
    private EditText mFeedbackTxt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feed_back);

        initialize();

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });


    }


    private void initialize() {

        mToolbar = (Toolbar) findViewById(R.id.send_feedback_toolbar);
        mSendBtn = (Button) findViewById(R.id.send_feedback_via_email_btn);
        mFeedbackTxt = (EditText) findViewById(R.id.feedback_msg_txt);


        mToolbar.setTitle("Help / Send Feedback");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }


    private void sendFeedback() {

        if(mFeedbackTxt.getText().toString().length() == 0) {
            Toast.makeText(SendFeedBackActivity.this, "Please write a message to send", Toast.LENGTH_SHORT).show();
            return;
        }

            Intent Email = new Intent(Intent.ACTION_SEND);
            Email.setType("text/email");
            Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "tbzvidzwadev@gmail.com" });
            Email.putExtra(Intent.EXTRA_TEXT, "Dear Collabo Team" + "\n\n" + mFeedbackTxt.getText().toString());
            startActivity(Intent.createChooser(Email, "Send Help/Feedback:"));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



}
