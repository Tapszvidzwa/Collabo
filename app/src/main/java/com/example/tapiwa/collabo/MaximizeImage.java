package com.example.tapiwa.collabo;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MaximizeImage extends AppCompatActivity {


    private TextView tag;
    private ImageView imageView;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maximizeimage);


        String title = getIntent().getStringExtra("title");
        String image = getIntent().getStringExtra("image");
        String profilename = getIntent().getStringExtra(("name"));
        tag = (TextView) findViewById(R.id.imageTag);
        name = (TextView) findViewById(R.id.profileName);
        imageView = (ImageView) findViewById(R.id.grid_item_image);
        tag.setText(title);
        name.setText(profilename);

        Picasso.with(this).load(image).into(imageView);


    }

}
