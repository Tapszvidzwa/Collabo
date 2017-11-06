package com.example.tapiwa.collegebuddy.classContents.notes;


import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Main.MainFrontPage;
import com.example.tapiwa.collegebuddy.R;
import com.example.tapiwa.collegebuddy.classContents.classContentsMain.ClassContentsMainActivity;
import com.example.tapiwa.collegebuddy.classContents.notes.SelectUsers.SelectUsers;
import com.example.tapiwa.collegebuddy.miscellaneous.GenericServices;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import es.dmoral.toasty.Toasty;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NotesFragment extends Fragment  {

    public static ListView listview;
    public static ArrayList<String> notesList;
    public static NotesListAdapter notesAdapter;
    private String className;
    public static NotesSQLiteDBHelper dbHelper;
    private View notesView;
    private ViewGroup viewGroup;
    public static int selectedNote;


    public NotesFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        notesView = inflater.inflate(R.layout.notes_fragment, container, false);
        className = ClassContentsMainActivity.className;
        viewGroup = (ViewGroup) notesView.findViewById(R.id.notesFragment);

        initialize();
        populateScreen();



        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                String note = (String) parent.getItemAtPosition(position);
               //  String contents = dbHelper.getNoteContents(note);

                selectedNote = position;

                //Pass the image title and full_image_uri to DetailsActivity

                //initial activity
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
        notesList = new ArrayList<>();
        dbHelper = new NotesSQLiteDBHelper(getApplicationContext());
        registerForContextMenu(listview);
    }

    public void populateScreen() {
        notesList = dbHelper.getAllTitles(className);


        Collections.reverse(notesList);
        notesAdapter = new NotesListAdapter(getApplicationContext(),R.layout.note_item_list, notesList, className);
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

    public void selectUserToSend() {
        Intent openUsers = new  Intent (getActivity(), SelectUsers.class);
        openUsers.putExtra("callingIntent", "notesFragment");
        openUsers.putExtra("Url", "nothing");
        startActivity(openUsers);
    }

    private void printNote() throws IOException, DocumentException {

      String title = notesList.get(selectedNote);
        String contents = dbHelper.getNoteContents(ClassContentsMainActivity.className, title);

        GenericServices.createPDF(title,contents,getActivity());


    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        selectedNote = info.position;
        switch(item.getItemId()){
            case R.id.delete_note:
               deleteNote(info.position);
                return true;
            case R.id.change_card_color:
                changeColor();
                break;
            case R.id.print_note_item:
                try {
                    printNote();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.send_inbox:
                selectUserToSend();
                break;
            case R.id.create_note_pdf:
                createNotePdf(info.position);
            default:
                return super.onContextItemSelected(item);
        }

        return true;
    }

    private void createNotePdf(int pos) {
        Toasty.info(getContext(), "Creating pdf...", Toast.LENGTH_LONG).show();
        String title = notesList.get(pos);
        String content = dbHelper.getNoteContents(ClassContentsMainActivity.className, title);
        try {
            GenericServices.saveNotePdf(title, content, getActivity(), ClassContentsMainActivity.projectKey);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void deleteNote(int position) {
        String noteTitle = notesList.get(position);
        notesList.remove(position);
        Collections.reverse(notesList);

        dbHelper.deleteNote(ClassContentsMainActivity.className, noteTitle);

        notesAdapter = new NotesListAdapter(getApplicationContext(),R.layout.note_item_list, notesList, className);
        listview.setAdapter(notesAdapter);

        Toasty.success(getContext(),
                "Deleted",
                Toast.LENGTH_SHORT).show();
    }

    private void changeColor() {


        final String[] items = {
                "Blue", "White" , "Magenta", "Red", "Black", "Green" , "Cyan", "Yellow"
        };

        ListAdapter adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items){
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);

                return v;
            }
        };


        new AlertDialog.Builder(getActivity())
                .setTitle("Choose Color")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        switch (item) {
                            case 0:
                                updateNoteColor("blue");
                                break;
                            case 1:
                                updateNoteColor("white");
                                break;
                            case 2:
                                updateNoteColor("magenta");
                                break;
                            case 3:
                                updateNoteColor("red");
                                break;
                            case 4:
                                updateNoteColor("black");
                                break;
                            case 5:
                                updateNoteColor("green");
                                break;
                            case 6:
                                updateNoteColor("cyan");
                                break;
                            case 7:
                                updateNoteColor("yellow");
                                break;
                            //    break;
                        }

                        //...
                    }
                }).show();
    }


    private void updateNoteColor(String color) {

        AppUsageAnalytics.incrementPageVisitCount("Change_Note_Color");
     String contents = dbHelper.getNoteContents(ClassContentsMainActivity.className, notesList.get(selectedNote));
        dbHelper.updateNote(
                ClassContentsMainActivity.className,
                notesList.get(selectedNote),
                contents,
                GenericServices.timeStamp(),
                color
        );

        notesAdapter.notifyDataSetChanged();
        listview.setAdapter(notesAdapter);
    }




}


