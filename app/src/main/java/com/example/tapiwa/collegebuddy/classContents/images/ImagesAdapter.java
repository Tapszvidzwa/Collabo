package com.example.tapiwa.collegebuddy.classContents.images;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ImagesAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<NewImage> imageList;


    public ImagesAdapter(Context context, int layout, ArrayList<NewImage> imageList) {
        this.context = context;
        this.layout = layout;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
        TextView tag, time;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {

        final NewImage newImage = imageList.get(position);

        Picasso.with(context).load(newImage.getFull_image_uri()).fetch();

        View row = view;
        ViewHolder holder = new ViewHolder();


        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.tag  = (TextView) row.findViewById(R.id.tag);
            holder.imageView = (ImageView) row.findViewById(R.id.Image);
            holder.time = (TextView) row.findViewById(R.id.time_image_uploaded);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        holder.tag.setText(newImage.getTag());
        holder.time.setText(newImage.getTimeUploaded());

        final ImageView holderr = holder.imageView;


        Picasso.with(context)
                .load(newImage.getThumb_uri())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .priority(Picasso.Priority.HIGH)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        // Try again online if cache failed
                        Picasso.with(context)
                                .load(newImage.getThumb_uri())
                                .priority(Picasso.Priority.HIGH)
                                .into(holderr);
                    }
                });

        return row;
    }
}
