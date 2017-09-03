package com.example.tapiwa.collabo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


public class ChatsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private DatabaseReference mUsersDatabaseReference;
    private ArrayList<Message> MessagesList;
    private String currentuser;
    private LayoutInflater inflater;


    public ChatsAdapter(Context context, int layout, ArrayList<Message> MessagesList) {
        this.context = context;
        this.layout = layout;
        this.MessagesList = MessagesList;
        this.mUsersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
    }


    @Override
    public int getCount() {
        return MessagesList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return MessagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    private class ViewHolder {
        TextView message, time_sent, name;
        CircleImageView msg_sender_img;
    }


    @Override
    public  View getView(int position, View view, ViewGroup viewGroup) {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuser = user.getUid();

        View row = view;
        ViewHolder holder = new ViewHolder();
        final Message message = MessagesList.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


            if(message.getUid().equals(currentuser)) {

                if (row == null) {
                row = mInflater.inflate(R.layout.activity_chats_item_list_user, null);

                holder.message = (TextView) row.findViewById(R.id.user_chat_text_txtV);
                holder.time_sent = (TextView) row.findViewById(R.id.user_chat_time_sent_txtV);


                    row.setTag(holder);


                } else {
                    holder = (ViewHolder) row.getTag();
                }


                holder.message.setText(message.getMessage());
                holder.time_sent.setText(message.getTime_sent());

                return row;


            }
            else {


                if(row == null) {
                    row = mInflater.inflate(R.layout.activity_chats_item_list, null);


                    holder.message = (TextView) row.findViewById(R.id.chat_text_txtV);
                    holder.name = (TextView) row.findViewById(R.id.chat_user_name_txtV);
                    holder.time_sent = (TextView) row.findViewById(R.id.chat_time_sent_txtV);

                    row.setTag(holder);
                } else {
                    holder = (ViewHolder) row.getTag();
                }


                holder.message.setText(message.getMessage());
                holder.time_sent.setText(message.getTime_sent());
                holder.name.setText(message.getSent_from());

                return row;

            }
    }


    }


