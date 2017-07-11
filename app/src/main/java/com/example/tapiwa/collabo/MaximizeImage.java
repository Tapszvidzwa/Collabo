package com.example.tapiwa.collabo;


import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MaximizeImage extends AppCompatActivity {


    private TextView tag;
    private ImageView imageView;
    private TextView name;
    private ProgressBar progressBar;
    private FloatingActionButton deletePhoto;
    final String FB_DATABASE_PATH = "photos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maximizeimage);





        progressBar = (ProgressBar) findViewById(R.id.maximizeImageProgressbar);
        deletePhoto = (FloatingActionButton) findViewById(R.id.deleteImage);
        String title = getIntent().getStringExtra("title");
        final String imageUri = getIntent().getStringExtra("image");
        String profilename = getIntent().getStringExtra(("name"));
        tag = (TextView) findViewById(R.id.imageTag);
        name = (TextView) findViewById(R.id.profileName);
        imageView = (ImageView) findViewById(R.id.grid_item_image);
        tag.setText(title);
        name.setText(profilename);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);
        final StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri);
        final Query ImagesQuery = ref.child(FB_DATABASE_PATH).orderByChild("url").equalTo(imageUri);


//// TODO: 7/11/17 Implement this method in the firebase helper
        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                 //// TODO: 7/11/17 make sure it deletes from Storage too and give result to UI

                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MaximizeImage.this, "Collabo successfully deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MaximizeImage.this, "Collabo failed to delete" + exception, Toast.LENGTH_LONG).show();
             
                    }
                });

                ImagesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                            Snapshot.getRef().removeValue();
                        }
                        MaximizeImage.this.finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MaximizeImage.this, "Failed to delete Image", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        Picasso.with(this)
                .load(imageUri)
                .priority(Picasso.Priority.HIGH)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        // Try again online if cache failed
                        Picasso.with(getApplicationContext())
                                .load(imageUri)
                                .priority(Picasso.Priority.HIGH)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(MaximizeImage.this, "Slow loading, check network strength...", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    });
    }

}
