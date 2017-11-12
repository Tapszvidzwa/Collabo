package com.example.tapiwa.collegebuddy.Main.Goals;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Main.LoadingBar.LoadingBar;
import com.example.tapiwa.collegebuddy.Main.MainFrontPage;
import com.example.tapiwa.collegebuddy.Main.NewClass;
import com.example.tapiwa.collegebuddy.Main.NewFeatures.AddFeature;
import com.example.tapiwa.collegebuddy.Main.NewFeatures.NewFeature;
import com.example.tapiwa.collegebuddy.Main.NewFeatures.NewFeaturesAdapter;
import com.example.tapiwa.collegebuddy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

import static com.example.tapiwa.collegebuddy.Main.MainFrontPage.user;
import static com.example.tapiwa.collegebuddy.authentication.LoginActivity.permissionsRef;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by tapiwa on 10/5/17.
 */

public class GoalsFragment extends Fragment {

    private ListView goalsList;
    private ImageView restingDude;
    private TextView noGoalsText;
    private FirebaseDatabase firebaseDatabase;
    public static DatabaseReference goalsDbRef;
    private View goalsPageView;
    private LoadingBar loadingBar;
    private ArrayList<Goal> list;
    private GoalsAdapter adapter;
    private final String GOALS = "Goals";
    private FloatingActionButton addGoal, resetGoals;
    public static int completedGoals;
    public static int uncompletedGoals;
    private TextView percentage;
    private LinearLayout progressBar;
    private RelativeLayout relativeLayout;
    public boolean goalsObtained;
    private int totalinList;
    private int initialBarlength;

        public GoalsFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // Inflate the layout for this fragment
            goalsPageView = inflater.inflate(R.layout.fragment_goals, container, false);
            AppUsageAnalytics.incrementPageVisitCount("Goals");
            relativeLayout =(RelativeLayout) goalsPageView.findViewById(R.id.fragment_goal_layout);
            firebaseDatabase = FirebaseDatabase.getInstance();
            goalsDbRef = firebaseDatabase.getReference(GOALS).child(user);
            initializeViews();
          //  getGoalsFromFirebase();
            return goalsPageView;
    }





    private void initializeViews() {

        percentage = (TextView) goalsPageView.findViewById(R.id.percentage_completed);
        MainFrontPage.toolbar.setTitle("Weekly Goals");
        addGoal = (FloatingActionButton) goalsPageView.findViewById(R.id.addGoal);
        resetGoals = (FloatingActionButton) goalsPageView.findViewById(R.id.resetGoal);
        restingDude = (ImageView) goalsPageView.findViewById(R.id.resting_dude);
        noGoalsText = (TextView) goalsPageView.findViewById(R.id.no_goals_text);

        progressBar = (LinearLayout) goalsPageView.findViewById(R.id.progress_inner_bar);


        goalsList = (ListView) goalsPageView.findViewById(R.id.goals_lstV);
        list = new ArrayList<>();
        adapter = new GoalsAdapter(getApplicationContext(), R.layout.goal_list_item, list);
        //  goalsList.setAdapter(adapter);


        addGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewGoal();
            }
        });

        resetGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueResetingGoals();
                // loadingBar.updateCompletionBar(0, 0, outerProgressBar, percentage);
            }
        });


        goalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Goal goal = list.get(position);
                String goalKey = goal.getPushkey();

                String goalDescription = goal.getGoal();

                Goal completedGoal = new Goal(goalDescription, "completed", goalKey);

                if (!goal.getCompletion().equals("completed")) {
                    goalsDbRef.child(goalKey).setValue(completedGoal);
                }
            }
        });

    }

    public void continueResetingGoals() {

        final SweetAlertDialog dg = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);

        dg.setTitleText("Are you sure?")
                .setContentText("This will reset all goals")
                .setConfirmText("Yes,reset")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        goalsDbRef.removeValue();
                        dg.dismiss();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dg.dismiss();
            }
        });
        dg.show();

    }

    private void createNewGoal() {

        //Get title of folder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_keyboard_black_24px);
        builder.setTitle(("Enter the Goal"));

        int maxLength = 200;
        final EditText givenTitle = new EditText(getApplicationContext());
        givenTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        givenTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        givenTitle.setTextColor(Color.BLACK);
        givenTitle.setVisibility(View.VISIBLE);
        builder.setView(givenTitle);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(givenTitle.getText().toString().length() > 0) {

                    //push Goal to Firebase
                    String pushKey = goalsDbRef.push().getKey();
                    Goal goal = new Goal(givenTitle.getText().toString(), "uncompleted", pushKey);
                    goalsDbRef.child(pushKey).setValue(goal).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()) {
                                Toasty.success(getApplicationContext(), "Goal set!", Toast.LENGTH_SHORT).show();
                                restingDude.setVisibility(View.INVISIBLE);
                                noGoalsText.setVisibility(View.INVISIBLE);
                            } else {
                                Toasty.error(getApplicationContext(), "Failed to set Goal, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toasty.info(getApplicationContext(), "Please provide a goal description", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onResume(){
        super.onResume();
      getGoalsFromFirebase();
        relativeLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                initialBarlength = relativeLayout.getWidth();
            }
        });
    }





    private void getGoalsFromFirebase() {

        loadingBar = new LoadingBar();

        goalsDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //fetch data from firebase
                list.clear();
                completedGoals = 0;
                uncompletedGoals = 0;


                for (DataSnapshot Snapshot1 : dataSnapshot.getChildren()) {
                    Goal goals = Snapshot1.getValue(Goal.class);

                        if(goals.getCompletion().equals("completed")) {
                        ++completedGoals;
                     } else {
                        ++uncompletedGoals;
                     }


                    list.add(goals);

                }


                totalinList = list.size();

                goalsDbRef.keepSynced(true);

                adapter = new GoalsAdapter(getApplicationContext(), R.layout.goal_list_item, list);
                goalsList.setAdapter(adapter);

                loadingBar.updateCompletionBar(completedGoals, uncompletedGoals, initialBarlength, progressBar, percentage);
                congratulateIfCompleted();

                if(list.size() == 0) {
                    restingDude.setVisibility(View.VISIBLE);
                    noGoalsText.setVisibility(View.VISIBLE);
                }
            }

            private void congratulateIfCompleted() {

                if(getActivity() != null) {
                    if (percentage.getText().equals("100%")) {
                        final SweetAlertDialog sdg = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                        sdg.setTitleText("CONGRATULATIONS!");
                        sdg.setConfirmText("");
                        sdg.show();

                        Thread myThread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    sleep(1200);
                                    sdg.dismiss();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        myThread.start();
                    }

                    AppUsageAnalytics.incrementPageVisitCount("Goals_Accomplished");
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

