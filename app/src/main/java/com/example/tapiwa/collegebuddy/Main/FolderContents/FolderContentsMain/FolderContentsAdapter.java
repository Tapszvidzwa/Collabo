package com.example.tapiwa.collegebuddy.Main.FolderContents.FolderContentsMain;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.CameraGalleryUploads.NewImage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class
FolderContentsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<NewImage> imageList;

    public FolderContentsAdapter(Context context, int layout, ArrayList<NewImage> imageList) {
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
        TextView imageTag, time;
        ImageView imgThumb;
        CardView cardView;
    }

    @Override
    public  View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.imageTag = (TextView) row.findViewById(R.id.tags_tagline_tv);
            holder.time = (TextView)  row.findViewById(R.id.tags_time_uploaded_tv);
            holder.imgThumb = (ImageView) row.findViewById(R.id.private_folder_contents_imgV);
            holder.cardView = (CardView) row.findViewById(R.id.folder_contents_card);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        final NewImage newImage = imageList.get(position);

holder.cardView.setCardBackgroundColor(Color.WHITE);
        holder.imageTag.setText(newImage.getTag());
        holder.time.setText("created " + newImage.getTimeUploaded());

        //do the picasso stuff here
        final ImageView holderr = holder.imgThumb;

        Picasso.with(context)
                .load(newImage.getThumb_uri())
                .placeholder(R.drawable.ic_stat_smalliconcollabo)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.imgThumb, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError() {
                        // Try again online if cache failed
                        Picasso.with(context)
                                .load(newImage.getThumb_uri())
                                .placeholder(R.drawable.ic_stat_smalliconcollabo)
                                .into(holderr);
                    }
                });

        return row;
    }


}

