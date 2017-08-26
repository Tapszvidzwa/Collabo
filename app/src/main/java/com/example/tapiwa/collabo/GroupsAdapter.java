package com.example.tapiwa.collabo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<NewGroupUpload> GroupsList;

    public GroupsAdapter(Context context, int layout, ArrayList<NewGroupUpload> GroupsList) {
        this.context = context;
        this.layout = layout;
        this.GroupsList = GroupsList;
    }

    @Override
    public int getCount() {
        return GroupsList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return GroupsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    private class ViewHolder {
        TextView groupName;
        ImageView groupIcon;

    }


    @Override
    public  View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.groupName = (TextView) row.findViewById(R.id.group_name_tv);
            holder.groupIcon = (ImageView) row.findViewById(R.id.group_icon);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        NewGroupUpload group = GroupsList.get(position);
        holder.groupName.setText(group.getGroupName());


        return row;
    }


}

