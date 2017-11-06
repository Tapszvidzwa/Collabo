package com.example.tapiwa.collegebuddy.Main.Inbox;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Main.HomePageFragment;
import com.example.tapiwa.collegebuddy.Main.MainFrontPage;
import com.example.tapiwa.collegebuddy.Main.NewFeatures.AddFeature;
import com.example.tapiwa.collegebuddy.Main.NewFeatures.NewFeature;
import com.example.tapiwa.collegebuddy.Main.NewFeatures.NewFeaturesAdapter;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.classContents.DOCS.DOC;
import com.example.tapiwa.collegebuddy.classContents.images.MaximizePrivateImageActivity;
import com.example.tapiwa.collegebuddy.classContents.notes.DisplayNoteActivity;
import com.example.tapiwa.collegebuddy.miscellaneous.GenericServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;
import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;

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
    private ImageView cryingBaby;
    private TextView noInboxTxt;
    private Badger inboxNotification;
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
        registerForContextMenu(inboxList);
        list = new ArrayList<>();
        adapter = new InboxAdapter(getApplicationContext(), R.layout.new_feature_list_item, list);
        inboxList.setAdapter(adapter);

        cryingBaby = (ImageView) inboxView.findViewById(R.id.cryingBaby);
        noInboxTxt = (TextView) inboxView.findViewById(R.id.no_inbox_text);


        inboxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                InboxObject item = list.get(position);
                String itemType = item.getType();

                if(itemType.equals("note")) {

                    Intent openNote = new Intent(getActivity(), DisplayInboxNoteActivity.class);

                    openNote.putExtra("title", list.get(position).getTitle());
                    openNote.putExtra("noteContents", list.get(position).getContent());

                    startActivity(openNote);

                } else if(itemType.equals("image")) {


                    Intent maximizeImage = new Intent(getActivity(), MaximizePrivateImageActivity.class);
                    maximizeImage.putExtra("imageUri", item.getUrl());
                    maximizeImage.putExtra("tag", item.getTitle());
                    maximizeImage.putExtra("callingIntent", "inbox");
                    maximizeImage.putExtra("pushKey", item.getPushKey());
                    startActivity(maximizeImage);

                } else if(itemType.equals("pdf")) {

                    openPdfFile(position);


                }
            }
        });


    }

    private void openPdfFile(int position) {
        InboxObject item = list.get(position);
        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(item.getUrl());

        try {
            final File myFile = GenericServices.createNewPDFFile(getApplicationContext(), item.getTitle());

            if (!myFile.exists()) {
                myFile.createNewFile();
            }

            httpsReference.getFile(myFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {

                    //todo check if the device has a pdf viewer
                    Uri path = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", myFile);
                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(path, "application/pdf");
                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                    // Verify it resolves
                    PackageManager packageManager = getApplicationContext().getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(pdfIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;

                    if (isIntentSafe) {
                        startActivity(Intent.createChooser(pdfIntent, "Open pdf file"));
                        Toasty.info(getApplicationContext(), "Some pdf readers won't work", Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.error(getApplicationContext(), "No pdf reader installed", Toast.LENGTH_SHORT).show();
                    }

                }
            });


        } catch (IOException e) {

        }

    }

    private void deleteInbox(int position) {

        InboxObject item = list.get(position);

        inboxRef.child(MainFrontPage.user).child(item.getPushKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toasty.success(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.inbox_item_select_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()){
            case R.id.delete_inbox:
                deleteInbox(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }


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

                if(list.size() == 0) {

                    cryingBaby.setVisibility(View.VISIBLE);
                    noInboxTxt.setVisibility(View.VISIBLE);

                } else {
                    cryingBaby.setVisibility(View.INVISIBLE);
                    noInboxTxt.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}

