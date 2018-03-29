package com.example.tapiwa.collegebuddy.Main.Tasks;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Main.Tasks.LoadingBar.CompletionBar;
import com.example.tapiwa.collegebuddy.Main.HomePage.MainFrontPageActivity;
import com.example.tapiwa.collegebuddy.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by tapiwa on 10/5/17.
 */

public class TaskFragment extends Fragment {

    private ListView goalsList;
    public static ImageView restingDude;
    public static TextView noGoalsText;
    private LinearLayout progressBar, progressBarBorder;
    private View tasksPageView;
    private CompletionBar completionBar;
    private TextView percentageTxtV;
    private LinkedList<Task> tasksList;
    private TaskAdapter adapter;
    private RelativeLayout parentLayout;

    private final String GOALS = "Goals";
    public static FloatingActionButton addTask;

    private int totalTasks;
    private int uncompletedTasks;
    private int initialBarlength;

        public TaskFragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // Inflate the layout for this fragment
            tasksPageView = inflater.inflate(R.layout.fragment_tasks, container, false);
            AppUsageAnalytics.incrementPageVisitCount(getString(R.string.TaskFragment));

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            initialBarlength = display.getWidth();

            initializeViews();
            initializeVariables();
            getGoals();

            //open file  // getTask
            return tasksPageView;
    }


    private void initializeVariables() {
        tasksList = new LinkedList<>();
        uncompletedTasks = 0;
    }


    private void getGoals() {

        try {
            //open tasks file
            File tasksFile = new File(getApplicationContext().getFilesDir(), getString(R.string.Tasks_File));
            //create new file if the file does not exist
            tasksFile.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader(tasksFile));

            Gson gson = new Gson();

            TasksList list = gson.fromJson(br, TasksList.class);

            if(list != null) {
                tasksList = list.getTaskList();
                adapter = new TaskAdapter(getApplicationContext(),R.layout.item_goal_list, tasksList);
                goalsList.setAdapter(adapter);
                totalTasks = tasksList.size();
                uncompletedTasks = countUncompletedTasks();
                updateCompletionBar();
            }

        } catch (IOException e) {
            Toasty.error(getApplicationContext(), getString(R.string.Tasks_file_creation), Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }


    private void initializeViews() {

        MainFrontPageActivity.toolbar.setTitle(getString(R.string.TaskFragment));

        percentageTxtV = (TextView) tasksPageView.findViewById(R.id.percentage_completed);
        restingDude = tasksPageView.findViewById(R.id.resting_dude);
        addTask = tasksPageView.findViewById(R.id.add_task);
        noGoalsText = tasksPageView.findViewById(R.id.no_goals_text);
        parentLayout = tasksPageView.findViewById(R.id.fragment_tasks_layout);

        progressBar = tasksPageView.findViewById(R.id.progress_inner_bar);
        progressBarBorder = tasksPageView.findViewById(R.id.progress_outer_bar);
        goalsList = tasksPageView.findViewById(R.id.goals_lstV);
        adapter = new TaskAdapter(getApplicationContext(), R.layout.item_goal_list, tasksList);
        completionBar = new CompletionBar();


        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTask();
            }
        });


        goalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Task updatedTask = tasksList.get(i);

                if(!updatedTask.getStatus().equals("completed")) {
                    updatedTask.setStatus("completed");
                    tasksList.set(i, updatedTask);
                    adapter.notifyDataSetChanged();
                    --uncompletedTasks;

                    if (checkTasksCompletion()) {
                        final SweetAlertDialog dg = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                        dg.setTitleText(getString(R.string.congratulations)).setContentText(getString(R.string.congratulation_msg));
                        dg.show();
                        tasksList.clear();
                        totalTasks = 0;
                        uncompletedTasks = 0;
                    }

                    updateCompletionBar();
                }
            }
        });


    }

    private boolean checkTasksCompletion() {
        Iterator iter = tasksList.iterator();
         while(iter.hasNext()) {
             Task task = (Task) iter.next();
             if(task.getStatus().equals("uncompleted")) {
                 return false;
             }
         }
         return true;
    }

    private void updateCompletionBar() {

            int total = totalTasks;
            int uncompleted = uncompletedTasks;

            completionBar.updateCompletionBar(
                    total - uncompleted,
                     uncompleted,
                    initialBarlength,
                    progressBar,
                    percentageTxtV
                    );
    }


    private int countUncompletedTasks() {

        Iterator iter = tasksList.iterator();
        int i = 0;

        while(iter.hasNext()) {
            Task task = (Task) iter.next();
            if(task.getStatus().equals("uncompleted")) {
               ++i;
            }
        }

        return i;
    }



    public void addNewTask() {

        //Get title of new task
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_keyboard_black_24px);
        builder.setTitle(R.string.Add_new_task);

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

                    //create the new task
                    Task newTask = new Task();
                    newTask.setTask(givenTitle.getText().toString());
                    newTask.setStatus("uncompleted");

                    //add it to the tasks list
                    tasksList.add(newTask);
                    adapter.notifyDataSetChanged();

                    totalTasks = tasksList.size();
                    uncompletedTasks = countUncompletedTasks();
                    updateCompletionBar();
                } else {
                    Toasty.info(getApplicationContext(), "Please provide a task description", Toast.LENGTH_SHORT).show();
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

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        initialBarlength = display.getWidth();

    }

    @Override
    public void onPause() {
        super.onPause();

        TasksList list = new TasksList();

        list.setTaskList(tasksList);

        Gson gson = new Gson();
        String tasksJson = gson.toJson(list);

        FileOutputStream fos = null;
            try {
                //open tasks file
                File tasksFile = new File(getApplicationContext().getFilesDir(), getString(R.string.Tasks_File));
                //create new file if the file does not exist
                tasksFile.createNewFile();
                //save/write the tasks to the tasks.json file
                fos = new FileOutputStream(tasksFile);
                byte[] tasksFileBytes = tasksJson.getBytes();
                fos.write(tasksFileBytes);
                fos.flush();

            } catch (IOException e) {
                Toasty.error(getApplicationContext(), getString(R.string.Tasks_file_creation), Toast.LENGTH_SHORT);
                return;
            } finally {

                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
    }
}

