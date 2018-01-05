package com.example.tapiwa.collegebuddy.Main.GPA;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tapiwa.collegebuddy.R;

/**
 * Created by tapiwa on 9/13/17.
 */

public class GPAFragment extends Fragment {

    private View gpaView;


    public GPAFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        gpaView = inflater.inflate(R.layout.fragment_notes, container, false);

        return gpaView;

    }




}
