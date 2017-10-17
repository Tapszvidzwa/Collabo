package com.example.tapiwa.collegebuddy.Main;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tapiwa.collegebuddy.R;

/**
 * Created by tapiwa on 10/5/17.
 */

public class Calculator extends Fragment {



        public Calculator() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.calculator, container, false);
        }


    }

