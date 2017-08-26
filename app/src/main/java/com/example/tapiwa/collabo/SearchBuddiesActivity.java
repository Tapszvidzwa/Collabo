package com.example.tapiwa.collabo;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchBuddiesActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private EditText mSearchNametext;
    private RecyclerView mSearch_results;
    private DatabaseReference mSearchResultsDatabase;
    public Context context;
    private FloatingActionButton searchBuddiesFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_buddies);
        context =  getApplicationContext();

        mToolBar = (Toolbar) findViewById(R.id.search_buddies_toolbar);
        mToolBar.setTitle("Find new Buddies");
        searchBuddiesFab = (FloatingActionButton) findViewById(R.id.search_buddie_fab);
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSearchNametext = (EditText) findViewById(R.id.search_name_text);
        mSearch_results = (RecyclerView) findViewById(R.id.searched_buddies_list);
        mSearch_results.setHasFixedSize(true);
        mSearch_results.setLayoutManager(new LinearLayoutManager(this));

        mSearchResultsDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");

        mSearchResultsDatabase.keepSynced(true);



        mSearchNametext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_buddies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        searchBuddiesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchNametext.getWindowToken(), 0);

                String name_to_search = mSearchNametext.getText().toString().trim();
                search_buddies(name_to_search);

            }
        });



        mSearchNametext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearchNametext.getWindowToken(), 0);

                    String name_to_search = mSearchNametext.getText().toString().trim();
                    search_buddies(name_to_search);
                    return true;
                }
                return false;
            }
        });

    }




    public void search_buddies(String search_name) {

        FirebaseRecyclerAdapter<BuddieProfiles, SearchResultsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BuddieProfiles, SearchResultsViewHolder>(
                BuddieProfiles.class,
                R.layout.buddies_item_list,
                SearchResultsViewHolder.class,
                mSearchResultsDatabase.orderByChild("name")
                        .startAt(search_name)
                        .endAt(search_name + "\uf8ff")
        ) {

            @Override
            protected void populateViewHolder(SearchResultsViewHolder viewHolder, BuddieProfiles buddieProfile, final int position) {

                viewHolder.setName(buddieProfile.getName());
                viewHolder.setBio(buddieProfile.getBio());
                viewHolder.setImage(buddieProfile.getThumb_image(), context);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String uid = getRef(position).getKey();

                        Intent openProfile = new Intent(SearchBuddiesActivity.this, ProfileActivity.class);
                        openProfile.putExtra("uid", uid);
                        openProfile.putExtra("myProfile", "notMine");
                        openProfile.putExtra("intent", "search");
                        startActivity(openProfile);
                    }
                });

            }
        };

        mSearch_results.setAdapter(firebaseRecyclerAdapter);

    }


    public static class SearchResultsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public SearchResultsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name) {

            TextView userDisplayName = (TextView) mView.findViewById(R.id.search_buddie_name);
            userDisplayName.setText(name);

        }

        public void setImage(String thumb_uri, Context context) {

            if (!thumb_uri.equals("default")) {

                CircleImageView searched_user_profile_photo = (CircleImageView) mView.findViewById(R.id.search_buddie_photo);
                Picasso.with(context).load(thumb_uri)
                        .placeholder(R.drawable.new_default_image)
                        .into(searched_user_profile_photo);

            }
        }

        public void setBio(String bio) {
            TextView userBio = (TextView) mView.findViewById(R.id.search_buddie_bio);
            userBio.setText(bio);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}