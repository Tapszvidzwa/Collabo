package com.example.tapiwa.collegebuddy.Main.FolderContents.Notes;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.R;

import java.util.ArrayList;


public class NotesListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<String> noteList;
    private String className;



    public NotesListAdapter(Context context, int layout, ArrayList<String> noteList, String classType) {
        this.context = context;
        this.layout = layout;
        this.noteList = noteList;
        this.className = classType;
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView title;
        TextView last_time_updated;
        CardView notesItemCard;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.title = (TextView) row.findViewById(R.id.titleNote);
            holder.notesItemCard = (CardView) row.findViewById(R.id.note_card_item);
            holder.last_time_updated = (TextView) row.findViewById(R.id.note_last_updated_time);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        String note = noteList.get(position);

        NotesSQLiteDBHelper dbHelper = new NotesSQLiteDBHelper(context);
        String time_last_updated = dbHelper.getTimeUpdated(className, note);
        String card_color = dbHelper.getNoteColor(className, note);


        holder.title.setText(note);
        holder.last_time_updated.setText("last updated " + time_last_updated);

        if(card_color != null) {

            if(card_color.equals("black")) {
                holder.title.setTextColor(Color.WHITE);
            } else {
                holder.title.setTextColor(Color.BLACK);
            }

            switch (card_color) {
                case "blue":
                    holder.notesItemCard.setBackgroundColor(Color.rgb(52, 152, 219));
                    break;

                case "white":
                    holder.notesItemCard.setCardBackgroundColor(Color.rgb(236, 240, 241));
                    holder.last_time_updated.setTextColor(Color.BLACK);
                    break;

                case "magenta":
                    holder.notesItemCard.setCardBackgroundColor(Color.rgb(155, 89, 182));
                    break;

                case "red":
                    holder.notesItemCard.setCardBackgroundColor(Color.rgb(231, 76, 60));
                    break;

                case "black":
                    holder.notesItemCard.setCardBackgroundColor(Color.rgb(65,74,76));
                    break;

                case "green":
                    holder.notesItemCard.setCardBackgroundColor(Color.rgb(144,238,144));
                    break;

                case "cyan":
                    holder.notesItemCard.setCardBackgroundColor(Color.rgb(51,85,96));
                    break;

                case "yellow":
                    holder.notesItemCard.setCardBackgroundColor(Color.rgb(241, 196, 15));
                    break;

                default:
                    holder.notesItemCard.setCardBackgroundColor(Color.rgb(52,152,219));

            }
        } else {
            holder.notesItemCard.setCardBackgroundColor(Color.rgb(52,152,219));
            holder.title.setTextColor(Color.BLACK);
        }

        return row;
    }


}