package com.example.tapiwa.collegebuddy.Main.FolderContents.StackImages;

import android.content.Context;
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


public class StackViewAdapter extends BaseAdapter {

    private ArrayList<NewImage> mData;
    private Context mContext;
    private int layout;

    public StackViewAdapter(ArrayList<NewImage> data,int layout, Context context) {
        this.mData = data;
        this.mContext = context;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public NewImage getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textViewCard;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if(row == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
           //row = inflater.inflate(R.layout.stack_image_item, parent, false);

            holder.textViewCard = (TextView) row.findViewById(R.id.stack_card_txtView);
            holder.imageView = (ImageView) row.findViewById(R.id.stack_card_imageView);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final NewImage currentImage = mData.get(position);
        holder.textViewCard.setText(currentImage.getTag());

        //do the picassso stuff right here
        final ImageView holderr = holder.imageView;

        Picasso.with(mContext)
                .load(currentImage.getFull_image_uri())
                .fit()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .priority(Picasso.Priority.HIGH)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        // Try again online if cache failed
                        Picasso.with(mContext)
                                .load(currentImage.getFull_image_uri())
                                .priority(Picasso.Priority.HIGH)
                                .into(holderr);
                    }
                });

        return row;
    }

}
