package com.example.tapiwa.collabo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
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
        ProgressBar progressBar;
        TextView name, tag;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {

        final ImageUpload imageUpload = imageList.get(position);
        Picasso.with(context).load(imageUpload.getUrl()).fetch();

        View row = view;
        ViewHolder holder = new ViewHolder();


        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.name = (TextView) row.findViewById(R.id.name);
            holder.tag  = (TextView) row.findViewById(R.id.tag);
            holder.imageView = (ImageView) row.findViewById(R.id.Image);
            holder.progressBar = (ProgressBar) row.findViewById(R.id.image_item_list_progress_bar);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }



        holder.name.setText(imageUpload.getProfileName());

        holder.tag.setText(imageUpload.getTag());

        final ImageView holderr = holder.imageView;
        final ProgressBar holderrr = holder.progressBar;


        Picasso.with(context)
                .load(imageUpload.getUrl())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .priority(Picasso.Priority.HIGH)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holderrr.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        // Try again online if cache failed
                        Picasso.with(context)
                                .load(imageUpload.getUrl())
                                .into(holderr);
                        holderrr.setVisibility(View.GONE);

                    }
                });

        return row;
    }
}
