package com.example.tapiwa.collabo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.VIBRATOR_SERVICE;



public class Collabos extends Fragment {

    public Collabos() {
    }

    public GridView gridView;
    public ArrayList<ImageUpload> list;
    public ImageListAdapter adapter;
    final int UNREAD_MESSAGE = 1;
    final int OPENED_MESSAGE = 0;
    Vibrator vibrate;

    final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private ProgressDialog mProgress;
    private String image_tag;
    private DatabaseReference mDatabaseRef;
    final String FB_DATABASE_PATH = "photos";
    SharedPreferences usrName;
    Uri fileUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);

        ShortcutBadger.removeCount(getContext());

        View collabos = inflater.inflate(R.layout.collabos, container, false);
        gridView = (GridView) collabos.findViewById(R.id.gridview);
        list = new ArrayList<>();
        adapter = new ImageListAdapter(getContext(), R.layout.image_item_list, list);
        gridView.setAdapter(adapter);
        vibrate = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);
        mProgress = new ProgressDialog(getContext());


        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        mDatabaseRef.keepSynced(true);

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        usrName = PreferenceManager.getDefaultSharedPreferences(getContext());

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch image data from firebase
                list.clear();
                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    ImageUpload img = Snapshot1.getValue(ImageUpload.class);
                    list.add(img);
                }
                Collections.reverse(list);


                //init adapter
                adapter = new ImageListAdapter(getContext(), R.layout.image_item_list, list);
                gridView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });


        FloatingActionButton takePhoto = (FloatingActionButton) collabos.findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TakePicture();
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                ImageUpload item = (ImageUpload) parent.getItemAtPosition(position);
                //Pass the image title and url to DetailsActivity
                Intent intent = new Intent(getContext(), MaximizeImage.class);
                intent.putExtra("title", item.getTag());
                intent.putExtra("image", item.getUrl());
                intent.putExtra("chatRoom", item.getChatRoom());
                intent.putExtra("name", item.getProfileName());
                intent.putExtra("activityCalling", "collabos");
                intent.putExtra("time", item.getTimeUploaded());
                intent.putExtra("user", "none");                //user is none because this is for the group

                //Start details activity
                startActivity(intent);
            }
        });


        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           int position, long id) {

                final ImageUpload item = (ImageUpload) parent.getItemAtPosition(position);
                vibrate.vibrate(40);

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }
                builder.setTitle("Delete Collabo")
                        .setMessage("Are you sure you want to delete this Collabo?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                //// TODO: 7/11/17 Please refactor this long code and also try to implement it in FirebaseHelper
                                String imageUri = item.getUrl();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.keepSynced(true);
                                final StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri);
                                final Query ImagesQuery = ref.child(FB_DATABASE_PATH).orderByChild("url").equalTo(imageUri);


                                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Collabo successfully deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(getContext(), "Collabo failed to delete" + exception, Toast.LENGTH_LONG).show();

                                    }
                                });

                                ImagesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                                            Snapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(getContext(), "Failed to delete Image", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });

        return collabos;
    }

    @Override
    public void onResume(){
        super.onResume();
        ShortcutBadger.removeCount(getContext());
    }


    public static String getTime() {

        DateTime dt = new DateTime();
        String timeNow = dt.toString().substring(11,16);
        int month = dt.monthOfYear().get();
        String date = dt.dayOfMonth().getAsShortText();


        return  "(" + month + "/" + date + ")" + " " + timeNow;
    }


    public void startUpload() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(("Provide the Collabo tag"));

        int maxLength = 40;
        final EditText tag = new EditText(getContext());
        tag.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        tag.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(tag);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: 7/3/17 fix such that user cannot enter empty tag
                image_tag = tag.getText().toString();
                attemptImageUpload();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(getContext(), "Upload cancelled, no tag", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            startUpload();
        }
    }

    public void attemptImageUpload() {
        mProgress.setMessage("Uploading Collabo...");
        mProgress.show();
        //Upload the picture to the Photo folder in the Storage bucket
        //// TODO: 6/29/17 change the uri so that its custom for every photo


        StorageReference filepath = storageReference.child("Photo").child(fileUri.getLastPathSegment());

        filepath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress.dismiss();

                String userName = "@" + usrName.getString("example_text", null);

                //send notifications to all users in group
                try {
                    sendNotifications(userName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getContext(), "Uploading finished", Toast.LENGTH_SHORT).show();
                String chatroom = createChatRoomName(image_tag);

                ImageUpload imageUpload = new ImageUpload(userName, image_tag, taskSnapshot.getDownloadUrl().toString(), getTime(), chatroom, UNREAD_MESSAGE);


                //save image info into the firebase database
                String uploadId = mDatabaseRef.push().getKey();
                mDatabaseRef.child(uploadId).setValue(imageUpload);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgress.dismiss();
                Toast.makeText(getContext(), "Uploading failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public String createChatRoomName(String tag) {
        return usrName.getString("example_text", null).trim() + tag;
    }

    private  void TakePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);

                fileUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private  File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }


    public static void sendNotifications(String username) throws IOException {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("userName", username)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.43.229/test/pushNotifications.php")
                .post(body)
                .build();

        client.newCall(request)
                .enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                    }
                });

    }

    private void createChatRoom() {

         DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

        Map<String, Object> map = new HashMap<String, Object>();

        map.put(usrName + image_tag, "");
        root.updateChildren(map);

    }

}



