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
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TagsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<ImageUpload> imageList;


    public TagsAdapter(Context context, int layout, ArrayList<ImageUpload> imageList) {
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
        TextView tag, name;
        ImageView tag_thumb_img;
        AVLoadingIndicatorView unreadMessageNotification;
    }



    @Override
    public  View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.tag = (TextView) row.findViewById(R.id.tags_tagline_tv);
            holder.tag_thumb_img = (ImageView) row.findViewById(R.id.tag_thumbail_imgV);
            holder.unreadMessageNotification = (AVLoadingIndicatorView) row.findViewById(R.id.unreadMessage_indicator);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.unreadMessageNotification.setVisibility(View.INVISIBLE);

        final ImageUpload imageUpload = imageList.get(position);

        holder.tag.setText(imageUpload.getTag());


        //do the picasso stuff for the uploaded image

        final ImageView holderr = holder.tag_thumb_img;
        Picasso.with(context)
                .load(imageUpload.getThumb_uri())
                .placeholder(R.mipmap.ic_launcher)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.tag_thumb_img, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        // Try again online if cache failed
                        Picasso.with(context)
                                .load(imageUpload.getThumb_uri())
                                .placeholder(R.mipmap.ic_launcher)
                                .into(holderr);

                    }
                });


        return row;
    }


}

