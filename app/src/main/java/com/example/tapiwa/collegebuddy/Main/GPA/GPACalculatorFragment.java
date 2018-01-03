package com.example.tapiwa.collegebuddy.Main.GPA;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tapiwa.collegebuddy.R;

/**
 * Created by tapiwa on 10/5/17.
 */

public class GPACalculatorFragment extends Fragment {



        public GPACalculatorFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.gpa_calculator_fragment, container, false);
        }


    }

