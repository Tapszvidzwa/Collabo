package com.example.tapiwa.collegebuddy.classContents.assignments;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Notifications.NotificationReceiver;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.classContents.classContentsMain.ClassContentsMainActivity;
import com.example.tapiwa.collegebuddy.miscellaneous.GenericServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

public class AssignmentsFragment extends Fragment  {

    public static View assignmentsView;
    public static TextView date;
    public static ListView listview;
    public static ArrayList<Assignment> list;
    public static FragmentManager fragmentManager;
    public static String dueDateTitle;
    public static AssignmentsSQLiteDBHelper dbHelper;
    public static AssignmentsListAdapter assignmentsAdapter;
    public static FirebaseDatabase fireb;
    public static DatabaseReference ref;
    public static Date setDate;
    private static long ONE_DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24;

    public AssignmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        assignmentsView = inflater.inflate(R.layout.assignments_fragment, container, false);
        initialize();
        populateScreen();
        return assignmentsView;

    }

    public static void getDate(final FragmentManager fragmentManager) {

        final FragmentManager fgm = fragmentManager;


        // Initialize
        final SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                "Set due date and time",
                "Ok",
                "Cancel"
        );


// Assign values
        dateTimeDialogFragment.startAtCalendarView();
        dateTimeDialogFragment.set24HoursMode(false);
        dateTimeDialogFragment.setMinimumDateTime(new GregorianCalendar(2017, Calendar.NOVEMBER, 1).getTime());
        dateTimeDialogFragment.setMaximumDateTime(new GregorianCalendar(2050, Calendar.DECEMBER, 31).getTime());

       // dateTimeDialogFragment.

// Define new day and month format
        try {
            dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("dd MMMM", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

// Set listener
        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                String dueDate = date.toString();
                setDate = date;
                getDateToRemind(ClassContentsMainActivity.activity);
                dbHelper.insertDueDate(ClassContentsMainActivity.className, dueDateTitle, dueDate);
                AppUsageAnalytics.incrementPageVisitCount("DeadLines_Set");
                populateScreen();
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Date is get on negative button click
            }
        });

// Show
        dateTimeDialogFragment.show(fgm, "date");



    }

    public static void setCustomReminder(Date date, Activity activity, int numDays) {


        Intent reminderIntent = new Intent(activity, NotificationReceiver.class);
        reminderIntent.putExtra("assignmentTitle", dueDateTitle);
        reminderIntent.putExtra("numDaysLeft", numDays);

        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(getApplicationContext(), 345, reminderIntent, 0);

        Long dateDueTime = date.getTime();
        Long difference = dateDueTime - System.currentTimeMillis();

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + difference) - (ONE_DAY_IN_MILLISECONDS * numDays),
                pendingIntent);
    }

    public static void setDefaultReminder(Date date, Activity activity) {

        Intent reminderIntent = new Intent(activity, NotificationReceiver.class);
        reminderIntent.putExtra("assignmentTitle", dueDateTitle);
        reminderIntent.putExtra("numDaysLeft", 1);

        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(getApplicationContext(), 345, reminderIntent, 0);

        Long dateDueTime = date.getTime();
        Long difference = dateDueTime - System.currentTimeMillis();

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + difference) - ONE_DAY_IN_MILLISECONDS ,
                pendingIntent);
    }

    public static void getDateToRemind(Activity activity) {

        final CharSequence colors[] = new CharSequence[] {"1 Day", "2 Days", "3 Days", "4 Days", "5 Days"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Remind me before");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                // the user clicked on colors[which]
                String reminderDays = colors[position].toString().substring(0,1);
                int daysToRemind = Integer.parseInt(reminderDays);
                setCustomReminder(setDate, ClassContentsMainActivity.activity, daysToRemind);
                Toasty
                        .info(getApplicationContext(), "Reminder set " + daysToRemind + " days before deadline", Toast.LENGTH_SHORT)
                        .show();


            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
               ClassContentsMainActivity.activity.recreate();
            }
        });
        builder.show();
    }


    public static void getTitleOfAssignment(Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(("Enter the assignment title"));
        builder.setIcon(R.drawable.ic_keyboard_black_24px);

        int maxLength = 40;
        final EditText tag = new EditText(activity);
        tag.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        tag.setInputType(InputType.TYPE_CLASS_TEXT);
        tag.setTextColor(Color.BLACK);
        tag.setVisibility(View.VISIBLE);
        builder.setView(tag);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dueDateTitle = tag.getText().toString();
                getDate(fragmentManager);
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

    private void initialize() {
        fragmentManager = getFragmentManager();
        date = (TextView) assignmentsView.findViewById(R.id.date);
        String currentDay = GenericServices.dayOfTheWeek() + " " +GenericServices.date();
        date.setText(currentDay);
        listview = (ListView) assignmentsView.findViewById(R.id.assignmentsListView);
        list = new ArrayList<>();
        dbHelper = new AssignmentsSQLiteDBHelper(getApplicationContext());
        registerForContextMenu(listview);
    }

    public static void populateScreen() {
        list = dbHelper.getAllDates(ClassContentsMainActivity.className);
        assignmentsAdapter = new AssignmentsListAdapter(getApplicationContext(),R.layout.assignment_item_list, list);
        listview.setAdapter(assignmentsAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.assignments_list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.delete_assignment:
                deleteDueDate(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteDueDate(int position) {
        Assignment assignment = list.get(position);
        list.remove(position);
        Collections.reverse(list);
        dbHelper.deleteAssignment(ClassContentsMainActivity.className, assignment.getTitle());

        assignmentsAdapter = new AssignmentsListAdapter(getApplicationContext(),R.layout.assignment_item_list, list);
        listview.setAdapter(assignmentsAdapter);

        Toasty.success(getContext(),
                "Deleted",
                Toast.LENGTH_SHORT).show();
    }

}


