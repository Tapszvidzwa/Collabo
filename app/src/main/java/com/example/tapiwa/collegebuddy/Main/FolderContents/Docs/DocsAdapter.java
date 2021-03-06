package com.example.tapiwa.collegebuddy.Main.FolderContents.Docs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.R;

import java.util.ArrayList;


public class DocsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<DOC> docsList;


    public DocsAdapter(Context context, int layout, ArrayList<DOC> docsList) {
        this.context = context;
        this.layout = layout;
        this.docsList = docsList;
    }

    @Override
    public int getCount() {
        return docsList.size();
    }

    @Override
    public Object getItem(int position) {
        return docsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView docIcon;
        TextView doc_name, doc_uploaded_time;
    }

    @Override
    public View getView(int position, final View view, ViewGroup viewGroup) {

        final DOC doc = docsList.get(position);

        View row = view;
        ViewHolder holder = new ViewHolder();


        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.docIcon = (ImageView) row.findViewById(R.id.doc_icon);
            holder.doc_name  = (TextView) row.findViewById(R.id.doc_title);
            holder.doc_uploaded_time = (TextView) row.findViewById(R.id.doc_time_uploaded);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        holder.doc_name.setText(doc.getDoc_name());
        holder.doc_uploaded_time.setText(doc.getDoc_date_created());

         if(doc.getDoc_type().equals("pdf")) {
             holder.docIcon.setBackgroundResource(R.drawable.ic_pdf_new);
         } else {
             holder.docIcon.setBackgroundResource(R.drawable.ic_doc_word);
             }


        return row;
    }
}
