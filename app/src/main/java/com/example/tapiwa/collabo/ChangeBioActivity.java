package com.example.tapiwa.collabo;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ChangeBioActivity extends AppCompatActivity {


    private TextInputLayout mNewStatus;
    private ProgressDialog mProgress;
    private Button mSaveChangesBtn;
    private Toolbar mToolbar;
    private FirebaseUser user;
    private DatabaseReference mDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bio);

        mNewStatus = (TextInputLayout) findViewById(R.id.change_bio);
        mSaveChangesBtn = (Button) findViewById(R.id.save_new_status);
        mToolbar = (Toolbar) findViewById(R.id.change_bio_toolbar);
        mProgress = new ProgressDialog(ChangeBioActivity.this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        mToolbar.setTitle("Change Bio");
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mDatabaseReference.keepSynced(true);

        mSaveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setTitle("Saving changes");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.setMessage("Please wait while we update bio...");
                mProgress.show();



                String newStatus =  mNewStatus.getEditText().getText().toString().trim();


                mDatabaseReference.child("bio").setValue(newStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {
                            mProgress.dismiss();
                            Toast toast = Toast.makeText(ChangeBioActivity.this, "Bio updated successfully", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            finish();

                        } else {
                            Toast toast = Toast.makeText(ChangeBioActivity.this, "Failed to change bio, please try again...", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
