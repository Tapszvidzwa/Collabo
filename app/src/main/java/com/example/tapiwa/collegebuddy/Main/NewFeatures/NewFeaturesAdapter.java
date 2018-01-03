package com.example.tapiwa.collegebuddy.Main.NewFeatures;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.R;

import java.util.ArrayList;


public class NewFeaturesAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<NewFeature> FeatureList;


    public NewFeaturesAdapter(Context context, int layout, ArrayList<NewFeature> FeatureList) {
        this.context = context;
        this.layout = layout;
        this.FeatureList = FeatureList;
    }

    @Override
    public int getCount() {
        return FeatureList.size();
    }

    @Override
    public Object getItem(int position) {
        return FeatureList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView text;
        ImageView star;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {

        final NewFeature newFeature = FeatureList.get(position);

        View row = view;
        ViewHolder holder = new ViewHolder();


        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.text  = (TextView) row.findViewById(R.id.feature_description);
            holder.star = (ImageView) row.findViewById(R.id.feature_type);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        holder.text.setText(newFeature.getFeature());

        switch (newFeature.getStarType()) {

            case "latest":
                holder.star.setImageResource(R.drawable.ic_favorite);
                break;
            case "old":
                holder.star.setImageResource(R.drawable.ic_star);
                break;
            default:
                holder.star.setImageResource(R.drawable.ic_star);
        }

        return row;
    }
}
