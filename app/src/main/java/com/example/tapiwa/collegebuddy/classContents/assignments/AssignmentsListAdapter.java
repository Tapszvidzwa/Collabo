package com.example.tapiwa.collegebuddy.classContents.assignments;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.classContents.notes.NotesSQLiteDBHelper;

import java.util.ArrayList;


public class AssignmentsListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Assignment> assignmentsList;

    public AssignmentsListAdapter(Context context, int layout, ArrayList<Assignment> assignments) {
        this.context = context;
        this.layout = layout;
        this.assignmentsList = assignments;
    }

    @Override
    public int getCount() {
        return assignmentsList.size();
    }

    @Override
    public Object getItem(int position) {
        return assignmentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView assignment_title;
        TextView assignment_due_date;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.assignment_title = (TextView) row.findViewById(R.id.assignment_title);
            holder.assignment_due_date = (TextView) row.findViewById(R.id.assignment_due_date);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        Assignment assignment = assignmentsList.get(position);
        String dueDate = assignment.getDueDate().substring(0,16);

        holder.assignment_title.setText(assignment.getTitle());
        holder.assignment_due_date.setText("Due: "+ dueDate);
        return row;
    }


}