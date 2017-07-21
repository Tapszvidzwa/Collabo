package com.example.tapiwa.collabo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;


public class PrivateTagListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<ImageUpload> imageList;

    public PrivateTagListAdapter(Context context, int layout, ArrayList<ImageUpload> imageList) {
        this.context = context;
        this.layout = layout;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    private class ViewHolder {
        TextView name, tag, time;
        AVLoadingIndicatorView unreadMessageNotification;
    }



    @Override
    public  View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.name = (TextView) row.findViewById(R.id.userName);
            holder.time = (TextView) row.findViewById(R.id.timeUploaded);
            holder.tag = (TextView) row.findViewById(R.id.tagLine);


            holder.unreadMessageNotification = (AVLoadingIndicatorView) row.findViewById(R.id.unreadMessage);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }



        ImageUpload imageUpload = imageList.get(position);

        holder.name.setText(imageUpload.getProfileName());
        holder.time.setText(imageUpload.getTimeUploaded());
        holder.tag.setText(imageUpload.getTag());

        return row;
    }


}

