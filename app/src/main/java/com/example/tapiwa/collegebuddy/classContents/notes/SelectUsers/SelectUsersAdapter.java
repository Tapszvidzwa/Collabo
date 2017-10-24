package com.example.tapiwa.collegebuddy.classContents.notes.SelectUsers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.Main.Inbox.InboxObject;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.authentication.NewUser;

import java.util.ArrayList;


public class SelectUsersAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<NewUser> ObjectsList;


    public SelectUsersAdapter(Context context, int layout, ArrayList<NewUser> ObjectsList) {
        this.context = context;
        this.layout = layout;
        this.ObjectsList = ObjectsList;
    }

    @Override
    public int getCount() {
        return ObjectsList.size();
    }

    @Override
    public Object getItem(int position) {
        return ObjectsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView username;
        ImageView star;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {

        final NewUser userObject = ObjectsList.get(position);

        View row = view;
        ViewHolder holder = new ViewHolder();


        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.username  = (TextView) row.findViewById(R.id.select_user_name);
            holder.star = (ImageView) row.findViewById(R.id.select_user_img);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }
        holder.username.setText(userObject.name);

        return row;
    }
}
