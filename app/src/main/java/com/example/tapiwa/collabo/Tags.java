package com.example.tapiwa.collabo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class Tags extends Fragment {

    public Tags() {
    }

    public ListView unopenedListview, openedlistview;
    public static final String MyPREFERENCES = "MyPrefs";
    public static ArrayList<ImageUpload> Firebaselist;
    public UnopenedTagListAdapter unopenedMessagesadapter;
    public OpenedTagListAdapter openedMessagesadapter;
    private DatabaseReference mDatabaseRef;
    final String FB_DATABASE_PATH = "photos";
    public static Boolean onStopCalled = false;
    public static Boolean isInForeGround = false;
    SortMessages sortMessages;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        sortMessages = new SortMessages(getContext());
       // sortMessages.restoreStoredMessagesPreference();

        View tags = inflater.inflate(R.layout.tags, container, false);
        unopenedListview = (ListView) tags.findViewById(R.id.unopenedlistView);
        openedlistview = (ListView) tags.findViewById(R.id.openedlistView);
        Firebaselist = new ArrayList<>();

        unopenedMessagesadapter = new UnopenedTagListAdapter(getContext(), R.layout.tag_item_list, Firebaselist);
        openedMessagesadapter = new OpenedTagListAdapter(getContext(), R.layout.tag_item_list, Firebaselist);

        unopenedListview.setAdapter(unopenedMessagesadapter);
        openedlistview.setAdapter(openedMessagesadapter);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        mDatabaseRef.keepSynced(true);


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                Firebaselist.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    ImageUpload img = Snapshot1.getValue(ImageUpload.class);
                    Firebaselist.add(img);
                }
                Collections.reverse(Firebaselist);


                //init
                unopenedMessagesadapter = new UnopenedTagListAdapter(getContext(), R.layout.tag_item_list, sortMessages.listOfUnopenedMessages(Firebaselist));


                openedMessagesadapter = new OpenedTagListAdapter(getContext(), R.layout.tag_item_list, sortMessages.listOfOpenedMessages(Firebaselist));
                unopenedListview.setAdapter(unopenedMessagesadapter);
                openedlistview.setAdapter(openedMessagesadapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        unopenedListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                ImageUpload item = (ImageUpload) parent.getItemAtPosition(position);

                //Pass the image title and url to DetailsActivity
                Intent intent = new Intent(getContext(), MaximizeImage.class);
                intent.putExtra("title", item.getTag());
                intent.putExtra("key", item.getKey());
                intent.putExtra("position", position);
                intent.putExtra("image", item.getUrl());
                intent.putExtra("name", item.getProfileName());
                intent.putExtra("time", item.getTimeUploaded());
                intent.putExtra("chatRoom", item.getChatRoom());
                intent.putExtra("user", "none");
                intent.putExtra("activityCalling", "tags");

                //Start details activity
                startActivity(intent);

                /* added part -------------------->*/
                sortMessages.addToStoredMessages(item.getKey());

                //refresh
                unopenedMessagesadapter = new UnopenedTagListAdapter(getContext(), R.layout.tag_item_list, sortMessages.listOfUnopenedMessages(Firebaselist));
                unopenedListview.setAdapter(unopenedMessagesadapter);

                openedMessagesadapter = new OpenedTagListAdapter(getContext(), R.layout.tag_item_list, sortMessages.listOfOpenedMessages(Firebaselist));
                openedlistview.setAdapter(openedMessagesadapter);


                /* till here-----------------------> */


            }
        });


        openedlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                ImageUpload item = (ImageUpload) parent.getItemAtPosition(position);

                //Pass the image title and url to DetailsActivity
                Intent intent = new Intent(getContext(), MaximizeImage.class);
                intent.putExtra("title", item.getTag());
                intent.putExtra("key", item.getKey());
                intent.putExtra("position", position);
                intent.putExtra("image", item.getUrl());
                intent.putExtra("name", item.getProfileName());
                intent.putExtra("time", item.getTimeUploaded());
                intent.putExtra("chatRoom", item.getChatRoom());
                intent.putExtra("user", "none");
                intent.putExtra("activityCalling", "tags");

                //Start details activity
                startActivity(intent);

                //refresh
                openedMessagesadapter = new OpenedTagListAdapter(getContext(), R.layout.tag_item_list, sortMessages.listOfOpenedMessages(Firebaselist));
                openedlistview.setAdapter(openedMessagesadapter);

                unopenedMessagesadapter = new UnopenedTagListAdapter(getContext(), R.layout.tag_item_list, sortMessages.listOfUnopenedMessages(Firebaselist));
                unopenedListview.setAdapter(unopenedMessagesadapter);
                /* till here-----------------------> */

            }
        });

        return tags;
    }

    @Override
    public void onResume() {
        super.onResume();
        onStopCalled = false;
        isInForeGround = true;

      /*  unopenedMessagesadapter = new UnopenedTagListAdapter(getContext(), R.layout.tag_item_list, sortMessages.listOfUnopenedMessages(Firebaselist));
        openedMessagesadapter = new OpenedTagListAdapter(getContext(), R.layout.tag_item_list, sortMessages.listOfOpenedMessages(Firebaselist));
        unopenedListview.setAdapter(unopenedMessagesadapter);
        openedlistview.setAdapter(openedMessagesadapter); */

      //  String onResume = "OnresumeCalled";
      //  sortMessages.restoreStoredMessagesPreference();
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            sortMessages.restoreStoredMessagesPreference();
        } catch (Exception e) {

        }
   //     onStopCalled = false;
  //  sortMessages.restoreStoredMessagesPreference();

    }

    @Override
    public void onPause() {
        super.onPause();
        isInForeGround = false;
    //    String onPause = "OnPauseCalled";
    //    sortMessages.savePreferences();
    }

    public void onStop() {
        super.onStop();
        sortMessages.savePreferences();
        onStopCalled = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  sortMessages.savePreferences();
    }

    public static void refresh() {

    }
}
