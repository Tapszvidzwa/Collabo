package com.example.tapiwa.collegebuddy.notes;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class NotesSQLiteDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ClassNotes.db";
    public static final String NOTES_TABLE_NAME = "personalNotes";
    public static final String NOTES_COLUMN_TITLE = "title";
    public static final String NOTES_COLUMN_CONTENT = "contents";
    public static final String NOTES_COLUMN_CLASS = "class";
    public static final String NOTES_TIME_COLUMN_CONTENT = "time";



    public NotesSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table personalNotes" +
                        "(id integer primary key, class text, title text,contents text, time text)"

                //// TODO: 7/9/17 check if text is the right type of text to be entered here
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS personalNotes");
        onCreate(db);
    }

    public void insertNote (String classtype, String title, String contents,String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("class", classtype);
        contentValues.put("title", title);
        contentValues.put("contents", contents);
        contentValues.put("time", time);
        db.insert("personalNotes", null, contentValues);
    }

    public String getNoteContents(String classtype, String title) {

        // TODO: 7/6/17 fix displayNote 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from personalNotes", null);
        //// TODO: 7/10/17 look for a better way to implement this
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if (res.getString(res.getColumnIndex(NOTES_COLUMN_TITLE)).equals(title) &&
                    res.getString(res.getColumnIndex(NOTES_COLUMN_CLASS)).equals(classtype)) {
                return res.getString(res.getColumnIndex(NOTES_COLUMN_CONTENT));
            }
            res.moveToNext();
        }
        return null;
    }


    public String getTimeUpdated(String classtype, String title) {

        SQLiteDatabase db = this.getReadableDatabase();
        String [] args = {classtype};
        Cursor res = db.rawQuery("select * from personalNotes where class = ?", args);

        //// TODO: 7/10/17 look for a better way to implement this
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if (res.getString(res.getColumnIndex(NOTES_COLUMN_TITLE)).equals(title)) {
                return res.getString(res.getColumnIndex(NOTES_TIME_COLUMN_CONTENT));
            }
            res.moveToNext();
        }
        return null;
    }


    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from personalNotes", null);
        return res;
    }

    // TODO: 7/6/17 fix this part
    public boolean updateNote (String classtype, String title, String contents, String time,String pinned) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("class", classtype);
        contentValues.put("title", title);
        contentValues.put("contents", contents);
        contentValues.put("time", time);
        String [] whereArgs = {title};
        db.update("personalNotes", contentValues, "title = ? ", whereArgs);
        return true;
    }

   //// TODO: 7/6/17 fix this part
    public void deleteNote (String Notetitle) {
       // String givenNote = Notetitle;
        SQLiteDatabase db = this.getReadableDatabase();
        String [] whereArgs = {Notetitle};
        db.delete("personalNotes", "title = ?", whereArgs);
    }

    public ArrayList<String> getAllTitles(String classType) {

        ArrayList<String> array_list = new ArrayList<String>();


            SQLiteDatabase db = this.getReadableDatabase();
            String[] args = {classType};
            Cursor res = db.rawQuery("select * from personalNotes where class = ?", args);
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex(NOTES_COLUMN_TITLE)));
                res.moveToNext();
            }
        return array_list;
    }

}
