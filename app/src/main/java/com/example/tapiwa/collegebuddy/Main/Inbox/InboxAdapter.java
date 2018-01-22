package com.example.tapiwa.collegebuddy.Main.Inbox;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.CameraGalleryUploads.NewImage;
import com.example.tapiwa.collegebuddy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class InboxAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<InboxObject> ObjectsList;
    private String sender_img;
    private DatabaseReference profPicsRef;
    private FirebaseDatabase firebaseDatabase;


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

      firebaseDatabase = FirebaseDatabase.getInstance();
      profPicsRef = firebaseDatabase.getReference(context.getString(R.string.profile_photos_db_ref));

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
        holder.time_sent.setText(inboxObject.getTime_sent());
        holder.sender_name.setText(inboxObject.getSenderName());

        final  CircleImageView holder1 = holder.sender_image;


        profPicsRef.child(inboxObject.getSenderID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    final NewImage sender_image = dataSnapshot.getValue(NewImage.class);

                    //load sender photo
                    Picasso.with(context)
                            .load(sender_image.getThumb_uri())
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
                                            .load(sender_image.getThumb_uri())
                                            .priority(Picasso.Priority.HIGH)
                                            .into(holder1);
                                }
                            });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



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
