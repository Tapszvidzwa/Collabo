package com.example.tapiwa.collegebuddy.Main.FolderContents.SelectUsers;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Main.Inbox.InboxObject;
import com.example.tapiwa.collegebuddy.Miscellaneous.GenericMethods;
import com.example.tapiwa.collegebuddy.Notifications.SendNotification;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.Authentication.NewUser;
import com.example.tapiwa.collegebuddy.Main.FolderContents.Docs.DOC;
import com.example.tapiwa.collegebuddy.Main.FolderContents.Docs.DocsFragment;
import com.example.tapiwa.collegebuddy.Main.FolderContents.FolderContentsMain.FolderContentsMainActivity;
import com.example.tapiwa.collegebuddy.Main.FolderContents.Images.ImagesFragment;
import com.example.tapiwa.collegebuddy.CameraGalleryUploads.NewImage;
import com.example.tapiwa.collegebuddy.Main.FolderContents.Notes.NotesSQLiteDBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import es.dmoral.toasty.Toasty;

import static com.example.tapiwa.collegebuddy.Main.FolderContents.Notes.NotesFragment.notesList;
import static com.example.tapiwa.collegebuddy.Main.FolderContents.Notes.NotesFragment.selectedNote;

/**
 * Created by tapiwa on 10/5/17.
 */

public class SelectUsersActivity extends AppCompatActivity  {

    private ListView usersListView;
    private Activity activity;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDbRef, profilePicsDbRef;
    private Toolbar toolbar;
    private Button searchButton;
    private SearchView usernameToSearch;
    private ArrayList<NewUser> users;
    private SelectUsersAdapter adapter;
    public static final String NOTE_TYPE = "note";
    private String currentUserName;
    public static String Url, callingIntent, noteTitle, noteContents, sender_image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        callingIntent = getIntent().getStringExtra("callingIntent");
        Url = getIntent().getStringExtra("Url");
        activity = this;
        noteTitle = getIntent().getStringExtra("noteTitle");
        noteContents = getIntent().getStringExtra("noteContents");
        currentUserName = GenericMethods.getThisUserName(activity);

        initializeViews();
        initializeFirebase();
        setOnCLickListeners();


    }

    private void initializeViews() {

        usersListView = (ListView) findViewById(R.id.users_listView);
        usernameToSearch = (SearchView) findViewById(R.id.user_to_search);
        toolbar = (Toolbar) findViewById(R.id.choose_user_toolbar);
        searchButton = (Button) findViewById(R.id.seach_user_button);

        users = new ArrayList<>();

        toolbar.setTitle("Choose Users");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        usernameToSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUser(newText);
                return false;
            }
        });



    }


    private void initializeFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");
        profilePicsDbRef = firebaseDatabase.getReference(getString(R.string.profile_photos_db_ref));
        usersDbRef.keepSynced(true);
    }

   private void searchUser(String name) {


       Query searchName = usersDbRef.orderByChild("name")
               .startAt(name)
               .endAt(name + "\uf8ff");


       searchName.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               //fetch image data from firebase
               users.clear();

               for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                   NewUser user = Snapshot1.getValue(NewUser.class);
                   users.add(user);
               }
               Collections.reverse(users);

               adapter = new SelectUsersAdapter(getApplicationContext(), R.layout.item_select_user, users);
               usersListView.setAdapter(adapter);
               adapter.notifyDataSetChanged();
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {
           }
       });





   }

    private void setOnCLickListeners() {


        profilePicsDbRef.child(GenericMethods.getThisUserID(activity))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        NewImage image = dataSnapshot.getValue(NewImage.class);
                        sender_image_uri = image.thumb_uri;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick (AdapterView < ? > parent, View v,int position, long id){
                //Do some stuff over here

                NewUser selectedUser = (NewUser) parent.getItemAtPosition(position);
                String selectedUserID = selectedUser.getUid();

                FirebaseDatabase database = FirebaseDatabase.getInstance();

                DatabaseReference sendToInbxRef = database
                        .getReference(getString(R.string.inbox_db_ref))
                        .child(selectedUserID);


                String pushKey = sendToInbxRef.push().getKey();


                if(callingIntent.equals(getString(R.string.notes_fragment))) {
                    //get note
                    String noteTitle = notesList.get(selectedNote);
                    NotesSQLiteDBHelper dbHelper = new NotesSQLiteDBHelper(getApplicationContext());

                    String content = dbHelper.getNoteContents(FolderContentsMainActivity.className, noteTitle);
                    String note_color = dbHelper.getNoteColor(FolderContentsMainActivity.className, noteTitle);

                    sendToInbxRef.child(pushKey)
                            .setValue(new
                                    InboxObject(currentUserName,
                                    noteTitle,
                                    content,
                                    note_color,
                                    NOTE_TYPE,
                                    pushKey,
                                    GenericMethods.timeStamp(),
                                    sender_image_uri));
                }
                else if(callingIntent.equals(getString(R.string.images_fragment))) {

                    NewImage img = ImagesFragment.list.get(ImagesFragment.selectedImage);


                    String aboutTosend = sender_image_uri;

                    InboxObject imageObject = new InboxObject(
                            currentUserName,
                            img.getTag(),
                            "image",
                            img.getFull_image_uri(),
                            pushKey,
                            GenericMethods.timeStamp(),
                            sender_image_uri);

                    sendToInbxRef.child(pushKey).setValue(imageObject);
                } else if(callingIntent.equals(getString(R.string.docs_fragment))) {

                    DOC doc = DocsFragment.list.get(DocsFragment.selectedDocument);
                    InboxObject docObject = new InboxObject(currentUserName,
                            doc.getDoc_name(),
                            "pdf",
                            doc.getDoc_uri(),
                            pushKey,
                            GenericMethods.timeStamp(),
                            sender_image_uri);

                    sendToInbxRef.child(pushKey).setValue(docObject);

                } else if(callingIntent.equals(getString(R.string.display_note_activity)) ||
                        callingIntent.equals(getString(R.string.newNote_activity))) {

                    sendToInbxRef.child(pushKey)
                            .setValue(new InboxObject(currentUserName,
                                    noteTitle,
                                    noteContents,
                                    "yellow",
                                    NOTE_TYPE,
                                    pushKey,
                                    GenericMethods.timeStamp(),
                                    sender_image_uri));
                }

                SendNotification sendNotification = new SendNotification(selectedUserID, currentUserName);
                sendNotification.executeSendNotification();



                Toasty.success(getApplicationContext(), "Sent to " + selectedUser.name, Toast.LENGTH_SHORT).show();
                SelectUsersActivity.this.finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameToSearch = usernameToSearch.getQuery().toString();
                searchUser(nameToSearch);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
