package com.example.tapiwa.collabo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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


public class ChangeDisplayNameActivity extends AppCompatActivity {


    private TextInputLayout mNewName;
    private ProgressDialog mProgress;
    private Button mSaveChangesBtn;
    private Toolbar mToolbar;
    private FirebaseUser user;
    private DatabaseReference mDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_display_name);

        mNewName = (TextInputLayout) findViewById(R.id.change_display_name);
        mSaveChangesBtn = (Button) findViewById(R.id.save_new_display_name);
        mToolbar = (Toolbar) findViewById(R.id.change_display_name_toolbar);
        mProgress = new ProgressDialog(ChangeDisplayNameActivity.this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        mToolbar.setTitle("Change Display Name");

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mDatabaseReference.keepSynced(true);

        mSaveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we update name...");
                mProgress.show();


                String newDisplayName =  mNewName.getEditText().getText().toString().trim();
                mDatabaseReference.child("name").setValue(newDisplayName).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {
                            mProgress.dismiss();
                            Toast toast = Toast.makeText(ChangeDisplayNameActivity.this, "Name updated successfully", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            finish();

                        } else {
                            Toast toast = Toast.makeText(ChangeDisplayNameActivity.this, "Failed to change name, please try again...", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    }
                });

            }
        });

    }
}
