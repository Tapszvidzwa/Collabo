package com.example.tapiwa.collabo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by tapiwa on 7/3/17.
 */

public class CollaboList extends AppCompatActivity {

    GridView gridView;
    ArrayList<ImageUpload> list;
    ImageListAdapter adapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        gridView = (GridView) findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new ImageListAdapter(this, R.layout.images, list);
        gridView.setAdapter(adapter);



    }
}
