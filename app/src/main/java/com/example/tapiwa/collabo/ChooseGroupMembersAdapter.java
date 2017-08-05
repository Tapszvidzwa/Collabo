package com.example.tapiwa.collabo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChooseGroupMembersAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<BuddieProfiles> BuddieProfilesList;

    public ChooseGroupMembersAdapter(Context context, int layout, ArrayList<BuddieProfiles> BuddieProfilesList) {
        this.context = context;
        this.layout = layout;
        this.BuddieProfilesList = BuddieProfilesList;
    }


    @Override
    public int getCount() {
        return BuddieProfilesList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return BuddieProfilesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    private class ViewHolder {
        TextView name, bio;
        CircleImageView imageView;
        ImageView check;
    }


    @Override
    public  View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();
        final BuddieProfiles buddieProfile = BuddieProfilesList.get(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.name = (TextView) row.findViewById(R.id.create_new_group_buddie_name_tv);
            holder.check = (ImageView) row.findViewById(R.id.item_checkBox);
            holder.bio = (TextView) row.findViewById(R.id.create_new_group_bio_tv);
            holder.imageView = (CircleImageView) row.findViewById(R.id.create_new_group_buddie_photo_imgV);
          //  holder.checkBox = (CheckBox) row.findViewById(R.id.choose_grp_members_checkbox);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }



        holder.name.setText(buddieProfile.getName());
        holder.bio.setText(buddieProfile.getBio());


        final ImageView holderr = holder.imageView;
        Picasso.with(context)
                .load(buddieProfile.getThumb_image())
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
                        .load(buddieProfile.getThumb_image())
                        .placeholder(R.drawable.new_default_image)
                        .into(holderr);

            }
        });




        return row;
    }


}

