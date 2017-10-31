package com.example.tapiwa.collegebuddy.Main.Vocabulary;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.Main.Inbox.InboxObject;
import com.example.tapiwa.collegebuddy.R;

import java.util.ArrayList;


public class WordsListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<VocabDatum> ObjectsList;


    public WordsListAdapter(Context context, int layout, ArrayList<VocabDatum> ObjectsList) {
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
        TextView word;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {

        final VocabDatum vocabDatum = ObjectsList.get(position);

        View row = view;
        ViewHolder holder = new ViewHolder();


        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.word  = (TextView) row.findViewById(R.id.root_word);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        holder.word.setText(vocabDatum.getWord());

        return row;
    }
}
