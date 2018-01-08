package com.example.tapiwa.collegebuddy.Main.Inbox;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.Main.NewFeatures.NewFeature;
import com.example.tapiwa.collegebuddy.R;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class InboxAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<InboxObject> ObjectsList;


    public InboxAdapter(Context context, int layout, ArrayList<InboxObject> ObjectsList) {
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
        TextView text;
        TextView sender_name;
        TextView time_sent;
        CircleImageView sender_image;
        CardView card;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {

        final InboxObject inboxObject = ObjectsList.get(position);

        View row = view;
        ViewHolder holder = new ViewHolder();


        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.text  =  row.findViewById(R.id.inbox_title);
            holder.time_sent = row.findViewById(R.id.inbox_time_received);
            holder.sender_name =  row.findViewById(R.id.inbox_sent_by);
            holder.sender_image =  row.findViewById(R.id.inbox_sender_img);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        holder.text.setText(inboxObject.getTitle());
        holder.time_sent.setText(inboxObject.getTitle());
        holder.sender_name.setText(inboxObject.getSenderName());

        final  CircleImageView holder1 = holder.sender_image;


        if(inboxObject.imageUri != null) {
            //load sender photo
            Picasso.with(context)
                    .load(inboxObject.imageUri)
                    .fit()
                    .placeholder(R.drawable.ic_user)
                    .priority(Picasso.Priority.HIGH)
                    .into(holder1, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            // Try again online if cache failed
                            Picasso.with(context)
                                    .load(inboxObject.imageUri)
                                    .fit()
                                    .priority(Picasso.Priority.HIGH)
                                    .into(holder1);
                        }
                    });
        }


    /*    if(inboxObject.getType().equals("pdf")) {
            holder.star.setImageResource(R.drawable.ic_pdf_new);
        } else if(inboxObject.getType().equals("image")) {
            holder.star.setImageResource(R.drawable.ic_image_file);
        } else if(inboxObject.getType().equals("note")) {
                holder.star.setImageResource(R.drawable.ic_sticky_note);
        } else {
            holder.star.setImageResource(R.drawable.ic_file_new);
        }
        */

        return row;
    }
}
