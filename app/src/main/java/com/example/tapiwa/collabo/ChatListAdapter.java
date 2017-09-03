package com.example.tapiwa.collabo;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<NewChat> NewChatList;
    public static ViewHolder holderr;
    public String  thumbUri;

    public ChatListAdapter(Context context, int layout, ArrayList<NewChat> NewChatList) {
        this.context = context;
        this.layout = layout;
        this.NewChatList = NewChatList;
    }

    @Override
    public int getCount() {
        return NewChatList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return NewChatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView name, lastSeen;
        CircleImageView imageView;
        ImageView onlineStatus;
    }


    @Override
    public  View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.name = (TextView) row.findViewById(R.id.chat_item_list_name);
            holder.lastSeen = (TextView) row.findViewById(R.id.chats_fragment_last_seen);
            holder.imageView = (CircleImageView) row.findViewById(R.id.chat_item_list_image);
            holder.onlineStatus = (ImageView) row.findViewById(R.id.chats_fragment_online_status);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        final NewChat chatItemList = NewChatList.get(position);


        final String chatKey = chatItemList.getChatKey();
        final String imgThumb = chatItemList.getThumbUri();
        final String name = chatItemList.getName();

        holder.name.setText(name);
        holder.onlineStatus.setVisibility(View.VISIBLE);

        holderr = holder;


        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference onlinestatusCheck;

        onlinestatusCheck = database.getReference(OneToOneChats.ONLINE_STATUS).child(chatKey);


     onlinestatusCheck.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
             String status = dataSnapshot.getValue(OnlineStatus.class).status;

             if(status.equals("online")) {
                 holderr.lastSeen.setTextColor(Color.GREEN);
                 holderr.onlineStatus.setVisibility(View.VISIBLE);
             } else {
                 holderr.lastSeen.setTextColor(Color.BLACK);
                 holderr.onlineStatus.setVisibility(View.INVISIBLE);
             }

             holderr.lastSeen.setText(dataSnapshot.getValue(OnlineStatus.class).status);
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     });


       final ImageView holderr = holder.imageView;
        Picasso.with(context)
                .load(imgThumb)
                .placeholder(R.drawable.new_default_image)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
            }
            @Override
            public void onError() {
                // Try again online if cache failed
                Picasso.with(context)
                        .load(imgThumb)
                        .placeholder(R.drawable.new_default_image)
                        .into(holderr);
            }
        });


        return row;
    }


}

