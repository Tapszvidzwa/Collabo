package com.example.tapiwa.collabo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class BuddieProfilesAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<BuddieProfiles> BuddieProfilesList;

    public BuddieProfilesAdapter(Context context, int layout, ArrayList<BuddieProfiles> BuddieProfilesList) {
        this.context = context;
        this.layout = layout;
        this.BuddieProfilesList = BuddieProfilesList;
    }

    @Override
    public int getCount() {
        return BuddieProfilesList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return BuddieProfilesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    private class ViewHolder {
        TextView requestType;
        CircleImageView imageView;
    }


    @Override
    public  View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.requestType = (TextView) row.findViewById(R.id.request_buddie_name);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }



        BuddieProfiles buddieProfile = BuddieProfilesList.get(position);

        String name = buddieProfile.getName();

        holder.requestType.setText("@" + buddieProfile.getName() + " wants to be your buddie");

       /* Picasso.with(context).load().placeholder(R.drawable.new_default_image).into(holder.imageView); */
        return row;
    }


}

