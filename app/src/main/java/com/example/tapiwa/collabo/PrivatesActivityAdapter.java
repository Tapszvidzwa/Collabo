package com.example.tapiwa.collabo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;


public class PrivatesActivityAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<NewProjectFolder> projectNamesList;

    public PrivatesActivityAdapter(Context context, int layout, ArrayList<NewProjectFolder> projectNamesList) {
        this.context = context;
        this.layout = layout;
        this.projectNamesList = projectNamesList;
    }

    @Override
    public int getCount() {
        return projectNamesList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return projectNamesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    private class ViewHolder {
        TextView projectName;
        ImageView folderIcon;
    }

    @Override
    public  View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.projectName = (TextView) row.findViewById(R.id.private_folder_name);
            holder.folderIcon = (ImageView) row.findViewById(R.id.private_folder_item_list_icon);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        NewProjectFolder projectFolder = projectNamesList.get(position);

        holder.projectName.setText(projectFolder.getProjectName());

        return row;
    }


}

