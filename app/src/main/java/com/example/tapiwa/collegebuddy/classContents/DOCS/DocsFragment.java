package com.example.tapiwa.collegebuddy.classContents.DOCS;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Main.MainFrontPage;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.classContents.classContentsMain.ClassContentsMainActivity;
import com.example.tapiwa.collegebuddy.classContents.images.ImagesAdapter;
import com.example.tapiwa.collegebuddy.classContents.images.NewImage;
import com.example.tapiwa.collegebuddy.miscellaneous.GenericServices;
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
import com.itextpdf.text.DocumentException;

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
    public static DocsAdapter adapter;
    public static ListView docsListView;
    private View docsView;
    private ImageView noDocsImg;
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
        noDocsImg = (ImageView) docsView.findViewById(R.id.no_documents_img);
        noDocsTxtV = (TextView) docsView.findViewById(R.id.no_docs_txtV);

        registerForContextMenu(docsListView);
    }

    private void setListeners()  {

        docsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

                                Uri path = FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", myFile);
                                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                                pdfIntent.setDataAndType(path, "application/pdf");
                                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);startActivity(pdfIntent);

                            }
                        });


                    } catch (IOException e) {

                    }

            }
        });

    }

    public static void searchDocument(String documentName) {

        AppUsageAnalytics.incrementPageVisitCount("Search_Document");

        Query searchDoc = pdfDatabaseReference.child(MainFrontPage.user).orderByChild("doc_name")
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

        pdfDatabaseReference.child(MainFrontPage.user).addValueEventListener(new ValueEventListener() {
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void deletePdf(int pos) {
        DOC doc = list.get(pos);

        final String storageUrl = doc.getDoc_uri();

        pdfDatabaseReference.child(MainFrontPage.user)
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
        switch (item.getItemId()) {
            case R.id.delete_pdf:
                deletePdf(info.position);
                return true;
            case R.id.share_doc_pdf:
                sharePdf(info.position);
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
