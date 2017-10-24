package com.example.tapiwa.collegebuddy.Main.Inbox;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tapiwa.collegebuddy.Main.MainFrontPage;
import com.example.tapiwa.collegebuddy.Main.NewFeatures.AddFeature;
import com.example.tapiwa.collegebuddy.Main.NewFeatures.NewFeature;
import com.example.tapiwa.collegebuddy.Main.NewFeatures.NewFeaturesAdapter;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.classContents.notes.DisplayNoteActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.tapiwa.collegebuddy.authentication.LoginActivity.permissionsRef;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by tapiwa on 10/5/17.
 */

public class InboxFragment extends Fragment {

    private ListView inboxList;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference inboxRef;
    private View inboxView;
    private ArrayList<InboxObject> list;
    private InboxAdapter adapter;
    public static final String INBOX = "INBOX";

        public InboxFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // Inflate the layout for this fragment
            inboxView = inflater.inflate(R.layout.fragment_inbox, container, false);
            initializeViews();
            getInboxesFromFirebase();
            return inboxView;
    }



    private void initializeViews() {

        MainFrontPage.toolbar.setTitle("Inbox");

        inboxList = (ListView) inboxView.findViewById(R.id.inbox_lstV);
        list = new ArrayList<>();
        adapter = new InboxAdapter(getApplicationContext(), R.layout.new_feature_list_item, list);
        inboxList.setAdapter(adapter);


        inboxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openNote = new Intent(getActivity(), DisplayInboxNoteActivity.class);

                openNote.putExtra("title", list.get(position).getTitle());
                openNote.putExtra("noteContents", list.get(position).getContent());

                startActivity(openNote);
            }
        });


    }

    private void getInboxesFromFirebase() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        inboxRef = firebaseDatabase.getReference(INBOX);

        inboxRef.child(MainFrontPage.user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                list.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    InboxObject inboxObjects = Snapshot1.getValue(InboxObject.class);
                    list.add(inboxObjects);
                }

                Collections.reverse(list);
                inboxRef.keepSynced(true);

                adapter = new InboxAdapter(getApplicationContext(), R.layout.inbox_list_item, list);
                inboxList.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

