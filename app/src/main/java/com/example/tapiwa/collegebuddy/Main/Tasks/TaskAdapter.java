package com.example.tapiwa.collegebuddy.Main.Tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.R;

import java.util.ArrayList;
import java.util.LinkedList;


public class TaskAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private LinkedList<Task> taskList;


    public TaskAdapter(Context context, int layout, LinkedList<Task> taskList) {
        this.context = context;
        this.layout = layout;
        this.taskList = taskList;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView task;
        ImageView completion_status;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {

        final Task task = taskList.get(position);

        View row = view;
        ViewHolder holder = new ViewHolder();


        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.task = row.findViewById(R.id.task_title_txtV);
            holder.completion_status = row.findViewById(R.id.completion_status);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.task.setText(task.getTask());
        String taks = task.getTask().toString();


        switch (task.getStatus()) {
            case "completed":
                holder.completion_status.setImageResource(R.drawable.ic_tick);
                break;
            case "uncompleted":
                holder.completion_status.setImageResource(R.drawable.ic_blank_square);
                break;
            default:
                holder.completion_status.setImageResource(R.drawable.ic_blank_square);
        }

        return row;
    }
}
