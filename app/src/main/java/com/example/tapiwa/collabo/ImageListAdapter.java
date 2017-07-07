package com.example.tapiwa.collabo;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.SharedPreferences;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ImageListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<ImageUpload> imageList;

    public ImageListAdapter(Context context, int layout, ArrayList<ImageUpload> imageList) {
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
        TextView name, tag;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.name = (TextView) row.findViewById(R.id.name);
            holder.tag  = (TextView) row.findViewById(R.id.tag);
            holder.imageView = (ImageView) row.findViewById(R.id.Image);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        final ImageUpload imageUpload = imageList.get(position);

        holder.name.setText(imageUpload.getProfileName());

        holder.tag.setText(imageUpload.getTag());

        final ImageView holderr = holder.imageView;

        Picasso.with(context)
                .load(imageUpload.getUrl())
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
                                .load(imageUpload.getUrl())
                                .into(holderr);
                    }
                });

        return row;
    }
}
