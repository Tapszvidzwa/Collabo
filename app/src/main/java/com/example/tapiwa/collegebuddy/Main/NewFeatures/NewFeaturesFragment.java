package com.example.tapiwa.collegebuddy.Main.NewFeatures;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.tapiwa.collegebuddy.Main.HomePage.MainFrontPageActivity;
import com.example.tapiwa.collegebuddy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.tapiwa.collegebuddy.Authentication.LoginActivity.permissionsRef;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by tapiwa on 10/5/17.
 */

public class NewFeaturesFragment extends Fragment {

    private ListView featuresList;
    private FirebaseDatabase firebaseDatabase;
    public static DatabaseReference featuresDbRef;
    private View featuresPageView;
    private ArrayList<NewFeature> list;
    private NewFeaturesAdapter adapter;
    private Toolbar toolbar;
    private final String NEW_FEATURES = "NEW FEATURES";
    private FloatingActionButton addFeature;

        public NewFeaturesFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            featuresPageView = inflater.inflate(R.layout.fragment_new_features, container, false);
            initializeViews();
            getFeaturesFromFirebase();
            return featuresPageView;
    }



    private void initializeViews() {

        MainFrontPageActivity.toolbar.setTitle("New Features");
        addFeature = (FloatingActionButton) featuresPageView.findViewById(R.id.addFeatureBtn);

        featuresList = (ListView) featuresPageView.findViewById(R.id.new_features_lstV);
        list = new ArrayList<>();
        adapter = new NewFeaturesAdapter(getApplicationContext(), R.layout.item_new_features_list, list);
        featuresList.setAdapter(adapter);


        permissionsRef.child(MainFrontPageActivity.user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                int perm = Integer.parseInt(dataSnapshot.getValue().toString());
                if (perm == 1) {
                    addFeature.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        addFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateNewFeature();
            }
        });

    }

    private void openCreateNewFeature() {
        Intent openCreateNewFeature = new Intent(getActivity(), AddFeature.class);
        startActivity(openCreateNewFeature);
    }

    private void getFeaturesFromFirebase() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        featuresDbRef = firebaseDatabase.getReference(NEW_FEATURES);

        featuresDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                list.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    NewFeature features = Snapshot1.getValue(NewFeature.class);
                    list.add(features);
                }
                Collections.reverse(list);
                featuresDbRef.keepSynced(true);

                adapter = new NewFeaturesAdapter(getApplicationContext(), R.layout.item_new_features_list, list);
                featuresList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

