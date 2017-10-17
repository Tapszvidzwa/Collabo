package com.example.tapiwa.collegebuddy.classContents.notes;

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

            if(card_color.equals("black") || card_color.equals("blue")) {
                holder.title.setTextColor(Color.WHITE);
            } else {
                holder.title.setTextColor(Color.BLACK);
            }

            switch (card_color) {
                case "blue":
                    holder.notesItemCard.setCardBackgroundColor(Color.BLUE);
                    break;

                case "white":
                    holder.notesItemCard.setCardBackgroundColor(Color.WHITE);
                    break;

                case "magenta":
                    holder.notesItemCard.setCardBackgroundColor(Color.MAGENTA);
                    break;

                case "red":
                    holder.notesItemCard.setCardBackgroundColor(Color.RED);
                    break;

                case "black":
                    holder.notesItemCard.setCardBackgroundColor(Color.BLACK);
                    break;

                case "green":
                    holder.notesItemCard.setCardBackgroundColor(Color.rgb(0,255,0));
                    break;

                case "cyan":
                    holder.notesItemCard.setCardBackgroundColor(Color.CYAN);
                    break;

                case "yellow":
                    holder.notesItemCard.setCardBackgroundColor(Color.YELLOW);
                    break;

                default:
                    holder.notesItemCard.setCardBackgroundColor(Color.YELLOW);

            }
        } else {
            holder.notesItemCard.setCardBackgroundColor(Color.YELLOW);
            holder.title.setTextColor(Color.BLACK);
        }

        return row;
    }


}