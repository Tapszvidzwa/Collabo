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

import java.util.ArrayList;


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
        ImageView star;
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

            holder.text  = (TextView) row.findViewById(R.id.inbox_title);
            holder.card = (CardView) row.findViewById(R.id.inbox_card_item);
            holder.sender_name = (TextView) row.findViewById(R.id.inbox_sent_by);
            holder.star = (ImageView) row.findViewById(R.id.inbox_sender_img);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        holder.text.setText(inboxObject.getTitle());
        holder.sender_name.setText(inboxObject.getSenderName());

        String card_color = inboxObject.getNote_color();

        if(card_color != null) {

            if(card_color.equals("black") || card_color.equals("blue")) {
                holder.text.setTextColor(Color.WHITE);
            } else {
                holder.text.setTextColor(Color.BLACK);
            }

            switch (card_color) {
                case "blue":
                    holder.card.setCardBackgroundColor(Color.BLUE);
                    break;

                case "white":
                    holder.card.setCardBackgroundColor(Color.WHITE);
                    break;

                case "magenta":
                    holder.card.setCardBackgroundColor(Color.MAGENTA);
                    break;

                case "red":
                    holder.card.setCardBackgroundColor(Color.RED);
                    break;

                case "black":
                    holder.card.setCardBackgroundColor(Color.BLACK);
                    break;

                case "green":
                    holder.card.setCardBackgroundColor(Color.rgb(0,255,0));
                    break;

                case "cyan":
                    holder.card.setCardBackgroundColor(Color.CYAN);
                    break;

                case "yellow":
                    holder.card.setCardBackgroundColor(Color.YELLOW);
                    break;

                default:
                    holder.card.setCardBackgroundColor(Color.YELLOW);

            }
        } else {
            holder.card.setCardBackgroundColor(Color.YELLOW);
            holder.text.setTextColor(Color.BLACK);
        }







        return row;
    }
}
