package com.example.tapiwa.collabo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BuddiesListActivity extends AppCompatActivity {

    public ListView BuddiesListView;
    public static ArrayList<BuddieProfiles> Firebaselist;
    public BuddieFriendsAdapter adapter;
    private DatabaseReference mDatabaseRef;
    private String mCurrentUserId;
    private FirebaseUser mUser;
    private Toolbar mToolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddies_list);


        BuddiesListView = (ListView) findViewById(R.id.buddies_listview);
        Firebaselist = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mCurrentUserId = mUser.getUid();
        mToolbar = (Toolbar) findViewById(R.id.buddies_list_toolbar);
        mToolbar.setTitle("Your Buddies");
        adapter = new BuddieFriendsAdapter(getApplicationContext(), R.layout.buddies_item_list, Firebaselist);


        BuddiesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        BuddiesListView.setAdapter(adapter);



        mDatabaseRef = FirebaseDatabase.getInstance().getReference(ProfileActivity.FRIENDS_DATABASE_PATH).child(mCurrentUserId);
        mDatabaseRef.keepSynced(true);


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch from firebase
                Firebaselist.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    BuddieProfiles profiles = Snapshot1.getValue(BuddieProfiles.class);
                    Firebaselist.add(profiles);
                }

                adapter = new BuddieFriendsAdapter(getApplicationContext(), R.layout.buddies_item_list, Firebaselist);
                BuddiesListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        BuddiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SparseBooleanArray checked = BuddiesListView.getCheckedItemPositions();

                /*    ArrayList<BuddieProfiles> selectedItems = new ArrayList<String>();
                    for (int i = 0; i < checked.size(); i++) {
                        // Item position in mAdapter
                        int position = checked.keyAt(i);
                        // Add sport if it is checked i.e.) == TRUE!
                        if (checked.valueAt(i))
                            selectedItems.add(mAdapter.getItem(position));
                    }

                    String[] outputStrArr = new String[selectedItems.size()];

                    for (int i = 0; i < selectedItems.size(); i++) {
                        outputStrArr[i] = selectedItems.get(i);
                    }

                    Intent intent = new Intent(getApplicationContext(),
                            ResultActivity.class);

                    // Create a bundle object
                    Bundle b = new Bundle();
                    b.putStringArray("selectedItems", outputStrArr);

                    // Add the bundle to the intent.
                    intent.putExtras(b);

                    // start the ResultActivity
                    startActivity(intent);
                } */

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

}




























