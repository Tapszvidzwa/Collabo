package com.example.tapiwa.collabo;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MaximizeImage extends AppCompatActivity {


    private TextView tag;
    private ImageView imageView;
    private TextView name;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maximizeimage);


        progressBar = (ProgressBar) findViewById(R.id.maximizeImageProgressbar);
        String title = getIntent().getStringExtra("title");
        final String image = getIntent().getStringExtra("image");
        String profilename = getIntent().getStringExtra(("name"));
        tag = (TextView) findViewById(R.id.imageTag);
        name = (TextView) findViewById(R.id.profileName);
        imageView = (ImageView) findViewById(R.id.grid_item_image);
        tag.setText(title);
        name.setText(profilename);

        Picasso.with(this)
                .load(image)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .priority(Picasso.Priority.HIGH)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        // Try again online if cache failed
                        Picasso.with(getApplicationContext())
                                .load(image)
                                .into(imageView);
                        progressBar.setVisibility(View.GONE);

                    }
                });

    }

}
