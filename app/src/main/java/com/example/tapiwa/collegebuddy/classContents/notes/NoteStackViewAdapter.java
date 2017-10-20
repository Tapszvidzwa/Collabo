package com.example.tapiwa.collegebuddy.classContents.notes;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.classContents.images.NewImage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class NoteStackViewAdapter extends BaseAdapter {

    private ArrayList<NoteStackItem> mData;
    private Context mContext;
    private int layout;

    public NoteStackViewAdapter(ArrayList<NoteStackItem> data, int layout, Context context) {
        this.mData = data;
        this.mContext = context;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public NoteStackItem getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView title;
        TextView noteContent;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if(row == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.title = (TextView) row.findViewById(R.id.stack_note_title);
            holder.noteContent = (TextView) row.findViewById(R.id.stack_note_contents);


            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final NoteStackItem currentNote = mData.get(position);
        holder.title.setText(currentNote.getTitle());

        String color = currentNote.getColor();

        if(color != null) {

            switch (color) {
                case "red":
                    holder.title.setBackgroundColor(Color.RED);
                    break;
                case "green":
                    holder.title.setBackgroundColor(Color.GREEN);
                    break;
                case "magenta":
                    holder.title.setBackgroundColor(Color.MAGENTA);
                    break;
                case "black":
                    holder.title.setBackgroundColor(Color.BLACK);
                    holder.title.setTextColor(Color.WHITE);
                    break;
                case "white":
                    holder.title.setBackgroundColor(Color.WHITE);
                    break;
                case "yellow":
                    holder.title.setBackgroundColor(Color.YELLOW);
                    break;
                case "blue":
                    holder.title.setBackgroundColor(Color.BLUE);
                    holder.title.setTextColor(Color.WHITE);
                    break;
                case "cyan":
                    holder.title.setBackgroundColor(Color.CYAN);
                    break;
            }
        }

        holder.noteContent.setText(currentNote.getContents());


        return row;
    }

}
