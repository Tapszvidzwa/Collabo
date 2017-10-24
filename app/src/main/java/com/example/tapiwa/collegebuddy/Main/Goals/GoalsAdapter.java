package com.example.tapiwa.collegebuddy.Main.Goals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.Main.NewFeatures.NewFeature;
import com.example.tapiwa.collegebuddy.R;

import java.util.ArrayList;


public class GoalsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Goal> GoalList;


    public GoalsAdapter(Context context, int layout, ArrayList<Goal> GoalList) {
        this.context = context;
        this.layout = layout;
        this.GoalList = GoalList;
    }

    @Override
    public int getCount() {
        return GoalList.size();
    }

    @Override
    public Object getItem(int position) {
        return GoalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView goal;
        ImageView completion_status;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {

        final Goal goal = GoalList.get(position);

        View row = view;
        ViewHolder holder = new ViewHolder();


        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.goal  = (TextView) row.findViewById(R.id.goal_textView);
            holder.completion_status = (ImageView) row.findViewById(R.id.completion_status);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        holder.goal.setText(goal.getGoal());

        switch (goal.getCompletion()) {

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
