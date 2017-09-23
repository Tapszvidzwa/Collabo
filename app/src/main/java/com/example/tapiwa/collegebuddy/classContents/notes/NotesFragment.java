package com.example.tapiwa.collegebuddy.classContents.notes;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.classContents.ClassContentsMainActivity;

import java.util.ArrayList;
import java.util.Collections;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NotesFragment extends Fragment  {

    public static ListView listview;
    public static ArrayList<String> list;
    public static NotesListAdapter notesAdapter;
    private String className;
    public static NotesSQLiteDBHelper dbHelper;
    private View notesView;


    public NotesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        notesView = inflater.inflate(R.layout.notes_fragment, container, false);
        className = ClassContentsMainActivity.className;

        initialize();
        populateScreen();



        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                String note = (String) parent.getItemAtPosition(position);
               //  String contents = dbHelper.getNoteContents(note);

                //Pass the image title and full_image_uri to DetailsActivity
                Intent intent = new Intent(getApplicationContext(), DisplayNoteActivity.class);
                intent.putExtra("title", note);

                //Start details activity
                startActivity(intent);
            }
        });




        return notesView;

    }



    private void initialize() {
        listview = (ListView) notesView.findViewById(R.id.notesListView);
        list = new ArrayList<>();
        dbHelper = new NotesSQLiteDBHelper(getApplicationContext());
        registerForContextMenu(listview);

    }

    public void populateScreen() {
        list = dbHelper.getAllTitles(className);
        Collections.reverse(list);
        notesAdapter = new NotesListAdapter(getApplicationContext(),R.layout.note_item_list, list, className);
        listview.setAdapter(notesAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        populateScreen();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.note_list_item_menu, menu);

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.delete_folder:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }




}


