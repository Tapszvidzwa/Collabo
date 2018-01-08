package com.example.tapiwa.collegebuddy.Main.HomePage;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.eightbitlab.bottomnavigationbar.BottomBarItem;
import com.eightbitlab.bottomnavigationbar.BottomNavigationBar;
import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Main.Folder.ChooseClassActivity;
import com.example.tapiwa.collegebuddy.Main.Folder.ClassesAdapter;
import com.example.tapiwa.collegebuddy.Main.Folder.NewClass;
import com.example.tapiwa.collegebuddy.Main.Goals.GoalsFragment;
import com.example.tapiwa.collegebuddy.Main.Inbox.InboxFragment;
import com.example.tapiwa.collegebuddy.Main.NewFeatures.NewFeaturesFragment;
import com.example.tapiwa.collegebuddy.Main.UserProfile.UserProfileFragment;
import com.example.tapiwa.collegebuddy.Main.Vocabulary.DictionaryFragment;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.Settings.Settings;
import com.example.tapiwa.collegebuddy.Authentication.LoginActivity;
import com.example.tapiwa.collegebuddy.CameraGalleryUploads.CameraGalleryUpload;
import com.example.tapiwa.collegebuddy.Miscellaneous.GenericMethods;
import com.example.tapiwa.collegebuddy.Miscellaneous.SendFeedBackActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.example.tapiwa.collegebuddy.Authentication.LoginActivity.permissionsRef;
import static com.example.tapiwa.collegebuddy.Main.FolderContents.FolderContentsMain.FolderContentsMainActivity.projectKey;

public class MainFrontPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ListView foldersListView;
    private String[] drawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    public static String CurrentFragment;
    private Vibrator vibrate;
    public ArrayList<NewClass> list;
    public ClassesAdapter adapter;
    public static DatabaseReference mDatabaseRef, mFolderContentsDBRef, mUserSessionsDBRef;
    public static FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private Toolbar mToolBar, mToolBarTwo;
    private ProgressDialog mProgress;
    private StorageReference storageReference;
    private String image_tag;
    final int REQUEST_IMAGE_CAPTURE = 1;
    final int IMAGE_PICK = 1001;
    public final static String USER_PRIVATE_LIST_OF_GROUPS = "List_Of_Private_User_Folders";
    public static final String USER_NUMBER_OF_SESSIONS = "Number_Of_Login_Sessions";
    public static String user;
    private int permissionCode;
    public static File photoFile = null;
    private String thumb_download_url = null;
    private FloatingActionButton createNewClass;
    public BottomNavigationBar bottomNavigationBar;
    public static Toolbar toolbar;
    public static Uri resultFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_front_page2);

        new FinishRegistrationTask().execute();
        ShortcutBadger.removeCount(this);

        toolbar = (Toolbar) findViewById(R.id.frnt_pg_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        CurrentFragment = "Home";
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_bar);

        //add the bottom nav bar icons
        BottomBarItem homeIcon = new BottomBarItem(R.drawable.ic_home_black_24px);
        BottomBarItem inboxIcon = new BottomBarItem(R.drawable.ic_inbox_black_24px);
        BottomBarItem cameraIcon = new BottomBarItem(R.drawable.ic_photo_cameraa);
        BottomBarItem goalsIcon = new BottomBarItem(R.drawable.ic_list);
        BottomBarItem dictionaryIcon = new BottomBarItem(R.drawable.ic_big_dictionary);

        bottomNavigationBar.addTab(homeIcon);
        bottomNavigationBar.addTab(inboxIcon);
        bottomNavigationBar.addTab(cameraIcon);
        bottomNavigationBar.addTab(goalsIcon);
        bottomNavigationBar.addTab(dictionaryIcon);


        //set bottom navigation bar listeners
        bottomNavigationBar.setOnSelectListener(new BottomNavigationBar.OnSelectListener() {
            @Override
            public void onSelect(int position) {


                switch (position) {

                    case 0:
                        openHome();
                        break;
                    case 1:
                        openInbox();
                        break;
                    case 2:
                        CameraGalleryUpload.takePicture(MainFrontPageActivity.this, "MainFrontPage");
                        break;
                    case 3:
                        openGoals();
                        break;
                    case 4:
                        openDictionary();
                        break;

                }




            }
        });

       bottomNavigationBar.setOnReselectListener(new BottomNavigationBar.OnReselectListener() {
           @Override
           public void onReselect(int position) {


               switch (position) {

                   case 0:
                       openHome();
                       break;
                   case 1:
                       openInbox();
                       break;
                   case 2:
                       CameraGalleryUpload.takePicture(MainFrontPageActivity.this, "MainFrontPage");
                       break;
                   case 3:
                       openGoals();
                       break;
                   case 4:
                       openDictionary();
                       break;

               }

           }
       });



        final android.app.Fragment fragment = new HomePageFragment();

// There are multiple `FragmentManagers`, be sure to always use the right one!
        FragmentManager manager = getFragmentManager();

// This starts the `FragmentTransaction`.
        FragmentTransaction transaction = manager.beginTransaction();

// Now you can define what happens in this transactions
// You can add/replace/remove/hide or show as many Fragments
// as you want in one `FragmentTransaction`.
// This command specifically adds the Fragment to the placeholder we defined
// in the layout of the Activity
        transaction.replace(R.id.fragment_place_holder, fragment);

// This commits the `FragmentTransaction`.
// Only after you call this will any changes be made
        transaction.commit();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mUserSessionsDBRef = database.getReference(USER_NUMBER_OF_SESSIONS);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        LoginActivity.connectPermissions();

        permissionsRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null) {
                    permissionsRef.child(mAuth.getCurrentUser().getUid().toString()).setValue(0);
                } else if(dataSnapshot.getValue().toString().equals("1")) {
                    permissionCode = 1;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

  /*  private void initializeViews() {

        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        foldersListView = (ListView) findViewById(R.id.privates_folders_lstV);
        list = new ArrayList<>();
        adapter = new ClassesAdapter(getApplicationContext(), R.layout.privates_folders_item_lst, list);
        foldersListView.setAdapter(adapter);
        registerForContextMenu(foldersListView);

    } */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        CameraGalleryUpload uploadImages = new CameraGalleryUpload(projectKey);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && CurrentFragment.equals(getString(R.string.profile_fragment))) {
            uploadImages.connectFirebaseCloud(getString(R.string.profile_fragment));
            uploadImages.attemptImageUpload(photoFile, resultFileUri,getApplicationContext(), getString(R.string.profile_fragment));
            return;
        }

        if (requestCode == IMAGE_PICK && resultCode == RESULT_OK && CurrentFragment.equals(getString(R.string.profile_fragment))) {

            try {
                photoFile = uploadImages.createImageFile(getApplicationContext());
                resultFileUri = data.getData();

                uploadImages.galleryImage = true;
                uploadImages.connectFirebaseCloud(getString(R.string.profile_fragment));
                uploadImages.attemptImageUpload(photoFile,
                        resultFileUri,
                        getApplicationContext(),
                        this.getString(R.string.profile_fragment));

            } catch (Exception e) {
            }
            return;
        }

        if (requestCode == IMAGE_PICK && resultCode == RESULT_OK) {
            Intent chooseClassToUploadImage = new Intent(MainFrontPageActivity.this, ChooseClassActivity.class);
            startActivity(chooseClassToUploadImage);
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent chooseClassToUploadImage = new Intent(MainFrontPageActivity.this, ChooseClassActivity.class);
            startActivity(chooseClassToUploadImage);
            return;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!CurrentFragment.equals("Home")) {
            openHome();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle("Collabo");
        AppUsageAnalytics.incrementPageVisitCount("MainFrontPage");
        AppUsageAnalytics.recordTime();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        //// TODO: 8/1/17 Change these settings to custom settings
        if (id == R.id.action_settings) {
            Intent settings = new Intent(MainFrontPageActivity.this, Settings.class);
            startActivity(settings);
        }

        if (id == R.id.send_feedback) {
            Intent feedback = new Intent(MainFrontPageActivity.this, SendFeedBackActivity.class);
            startActivity(feedback);
        }

        if(id == R.id.send_invite) {
            GenericMethods.sendInvitation(MainFrontPageActivity.this);
        }

        if(id == R.id.class_front_page_info) {
           showGrinnellTime();
        }

        if(id == R.id.sign_out) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            this.finish();
        }


        return super.onOptionsItemSelected(item);
    }

    private void showGrinnellTime() {

   if(permissionCode == 1) {


       String time = GenericMethods.timeStamp();

       Integer hour = Math.abs((Integer.parseInt(time.substring(0, 2)) - 8));
       if (hour < 12) {
           hour = 24 - hour;
       }

       String grinnellTime = hour + time.substring(3, 5) + "hrs";

       AppUsageAnalytics.incrementPageVisitCount("Front_Page_Information");
       SweetAlertDialog sdg = new SweetAlertDialog(MainFrontPageActivity.this, SweetAlertDialog.NORMAL_TYPE);
       sdg.setTitleText("Grinnell Time");
       sdg.setContentText(grinnellTime);
       sdg.setCancelable(true);
       sdg.show();


   }
    }

    private void openDictionary() {
        CurrentFragment = "Dictionary";
        android.app.Fragment fragment = new DictionaryFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_place_holder, fragment)
                .commit();
    }

    private void openInbox() {

        CurrentFragment = "Inbox";
            android.app.Fragment fragment = new InboxFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_place_holder, fragment)
                    .commit();
    }

    private void openGoals() {

        CurrentFragment = "Goals";
        android.app.Fragment fragment = new GoalsFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_place_holder, fragment)
                .commit();
    }

    private void openInboxFragment() {

        CurrentFragment = "Inbox";
        android.app.Fragment fragment = new InboxFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_place_holder, fragment)
                .commit();
    }

    private void openHome() {

        CurrentFragment = "Home";
        android.app.Fragment fragment = new HomePageFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_place_holder, fragment)
                .commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.inbox) {
            openInboxFragment();
        }



        if (id == R.id.home) {
           openHome();
        }


        if(id == R.id.profile) {
            android.app.Fragment fragment = new UserProfileFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_place_holder, fragment)
                    .commit();

        }

        if(id == R.id.new_features) {
            android.app.Fragment fragment = new NewFeaturesFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_place_holder, fragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public static class FinishRegistrationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            //initialize firebasetokens
            FirebaseMessaging.getInstance().subscribeToTopic("notifications");
            String token = FirebaseInstanceId.getInstance().getToken();


            OkHttpClient client = new OkHttpClient();

            okhttp3.RequestBody body = new FormBody.Builder()
                    .add("Uid", GenericMethods.getCurrentUid())
                    .add("Token", token)
                    .build();

            Request request = new Request.Builder()
                    .url("http://132.161.242.110/test/registerUserToCollabo.php")
                    .post(body)
                    .build();

            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
