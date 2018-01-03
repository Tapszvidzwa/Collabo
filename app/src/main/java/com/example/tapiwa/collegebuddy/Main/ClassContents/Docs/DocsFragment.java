package com.example.tapiwa.collegebuddy.Main.ClassContents.Docs;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Main.HomePage.MainFrontPageActivity;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.Main.ClassContents.ClassContentsMain.ClassContentsMainActivity;
import com.example.tapiwa.collegebuddy.Main.ClassContents.Notes.SelectUsers.SelectUsers;
import com.example.tapiwa.collegebuddy.Miscellaneous.GenericServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import es.dmoral.toasty.Toasty;

import static com.facebook.FacebookSdk.getApplicationContext;


public class DocsFragment extends android.support.v4.app.Fragment {

    public static StorageReference pdfStorageReference;
    public static DatabaseReference pdfDatabaseReference;
    public static final String DOCS_DATABASE_REF = "DOCS_REF";
    public static final String DOCS_STORAGE_REF = "DOCS_STORAGE";
    public static  ArrayList<DOC> list;
    private WebView pdfView;
    public static DocsAdapter adapter;
    public static ListView docsListView;
    private View docsView;
    private ImageView noDocsImg;
    public static int selectedDocument;
    private TextView noDocsTxtV;


    public DocsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        docsView = inflater.inflate(R.layout.docs_fragment, container, false);

        initializeViews();
        setListeners();
        loadDocsFromFirebase();

        return docsView;

    }

    private void initializeViews() {

        docsListView = (ListView) docsView.findViewById(R.id.docs_listV);
        list = new ArrayList<>();
        pdfView = (WebView) docsListView.findViewById(R.id.docs_pdfView);
        noDocsImg = (ImageView) docsView.findViewById(R.id.no_documents_img);
        noDocsTxtV = (TextView) docsView.findViewById(R.id.no_docs_txtV);

        registerForContextMenu(docsListView);
    }

    private void setListeners() {

        docsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final DOC doc = list.get(position);

                StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(doc.getDoc_uri());

                try {
                    final File myFile = GenericServices.createNewPDFFile(getContext(), doc.getDoc_name());

                    if (!myFile.exists()) {
                        myFile.createNewFile();

                        httpsReference.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

//
          //                     openFile(doc.getDoc_uri());


                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    private void openFile(String url) {


    //    pdfView.getSettings();
    //    pdfView.loadUrl(url;

   // pdfView.fromUri(path);


 /* //  Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
/                                    pdfIntent.setDataAndType(path, "application/pdf");
                                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    // Verify it resolves
    PackageManager packageManager = getApplicationContext().getPackageManager();
    List<ResolveInfo> activities = packageManager.queryIntentActivities(pdfIntent, 0);
    boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
        startActivity(Intent.createChooser(pdfIntent, "Open pdf file"));
        Toasty.info(getApplicationContext(), "Some pdf readers will not work", Toast.LENGTH_SHORT).show();
    } else {
        Toasty.error(getApplicationContext(), "No pdf reader installed", Toast.LENGTH_SHORT).show();
    } */

    }

    public static void searchDocument(String documentName) {

        AppUsageAnalytics.incrementPageVisitCount("Search_Document");

        Query searchDoc = pdfDatabaseReference.child(MainFrontPageActivity.user).orderByChild("doc_name")
                .startAt(documentName)
                .endAt(documentName + "\uf8ff");


        searchDoc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
             list.clear();

                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    DOC doc = Snapshot1.getValue(DOC.class);
                    list.add(doc);
                }
                Collections.reverse(list);


                adapter = new DocsAdapter(getApplicationContext(), R.layout.doc_item_list, list);
                docsListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void sharePdf(int position) {

        DOC doc = list.get(position);

        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(doc.getDoc_uri());

        try {
            final File myFile = GenericServices.createNewPDFFile(getContext(), "Collabo PDF");

            if (!myFile.exists()) {
                myFile.createNewFile();
            }

            httpsReference.getFile(myFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {



                    Intent email = new Intent(Intent.ACTION_SEND);
                    Uri path = FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", myFile);
                    email.putExtra(Intent.EXTRA_STREAM, path);
                    email.setType("message/rfc822");
                    startActivity(email);

                    AppUsageAnalytics.incrementPageVisitCount("Image_Pdfs_created");


                }
            });


        } catch (IOException e) {

        }

    }


    private void loadDocsFromFirebase() {

        pdfDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReference(DOCS_DATABASE_REF);

        pdfStorageReference = FirebaseStorage
                .getInstance()
                .getReference(DOCS_STORAGE_REF);

        pdfDatabaseReference.child(MainFrontPageActivity.user)
                .child(ClassContentsMainActivity.projectKey)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                list.clear();
                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    DOC doc = Snapshot1.getValue(DOC.class);
                    list.add(doc);
                }
                Collections.reverse(list);
                adapter = new DocsAdapter(getApplicationContext(), R.layout.doc_item_list, list);
                docsListView.setAdapter(adapter);

                if(list.size() == 0) {
                   noDocsImg.setVisibility(View.VISIBLE);
                    noDocsTxtV.setVisibility(View.VISIBLE);
                } else {
                    noDocsImg.setVisibility(View.INVISIBLE);
                    noDocsTxtV.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void shareInCollabo(int pos) {

        DOC doc = list.get(pos);
        String Url = doc.getDoc_uri();
        Intent selectUser = new Intent(getActivity(), SelectUsers.class);
        selectUser.putExtra("callingIntent", "DocsFragment");
        selectUser.putExtra("Url", Url);
        startActivity(selectUser);
    }


    private void deletePdf(int pos) {
        DOC doc = list.get(pos);

        final String storageUrl = doc.getDoc_uri();

        pdfDatabaseReference.child(MainFrontPageActivity.user)
                .child(MainFrontPageActivity.user)
                .child(doc.getDoc_key())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                StorageReference storageReference = FirebaseStorage
                        .getInstance()
                        .getReferenceFromUrl(storageUrl);

                storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toasty.success(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.docs_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

       selectedDocument = info.position;

        switch (item.getItemId()) {
            case R.id.delete_pdf:
                deletePdf(info.position);
                return true;
            case R.id.share_doc_pdf:
                sharePdf(info.position);
                return true;
            case R.id.share_doc_in_collabo:
                shareInCollabo(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        AppUsageAnalytics.incrementPageVisitCount("Docs_Fragment");
    }

}
