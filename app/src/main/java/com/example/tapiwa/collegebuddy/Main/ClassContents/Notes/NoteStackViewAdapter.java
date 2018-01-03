package com.example.tapiwa.collegebuddy.Main.ClassContents.Notes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.R;

import java.util.ArrayList;


public class NoteStackViewAdapter extends BaseAdapter {

    private ArrayList<NoteStackItem> mData;
    private Context mContext;
    private int layout;
    private boolean codeModeActivated = false;

    public NoteStackViewAdapter(ArrayList<NoteStackItem> data, int layout, Context context, boolean codeMode) {
        this.mData = data;
        this.mContext = context;
        this.layout = layout;
        this.codeModeActivated = codeMode;
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


    private void fontcolor(String text,int color, TextView noteContents) {

        Spannable raw=new SpannableString(noteContents.getText());

        int index= TextUtils.indexOf(raw, text);
        while (index >= 0) {
            raw.setSpan(new ForegroundColorSpan(color), index, index
                    + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            index=TextUtils.indexOf(raw, text, index + text.length());
        }
        noteContents.setText(raw);
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
        holder.noteContent.setText(currentNote.getContents());

        if(codeModeActivated) {

            holder.noteContent.setBackgroundColor(Color.DKGRAY);

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/sourcecodeproregular.ttf");
            holder.noteContent.setTypeface(typeface);
            holder.noteContent.setTextColor(Color.rgb(0,206,0));


            String [] keywordsOrange = {"String","string","Boolean", "boolean", "if", "for", "int", "new", "public", "private", ";",
            "return", "static", "while", "else", "catch", "try", "null", "case", "switch", ",", "Char", "char", "Integer",
            "Character", "HashMap", "HashSet", "Set", "throw", "long", "Double"};

            for(String y:keywordsOrange)
            {
                fontcolor(y,Color.rgb(255,165,0), holder.noteContent);
            }

            String [] keywordsPurple = {"true", "false", "=", "+" , "<", "<", "&", "%", "[", "]"};
            for(String y:keywordsPurple)
            {
                fontcolor(y,Color.rgb(214,82,148), holder.noteContent);
            }

        } else {


            String color = currentNote.getColor();

            if (color != null) {

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
        }


        return row;
    }

}
