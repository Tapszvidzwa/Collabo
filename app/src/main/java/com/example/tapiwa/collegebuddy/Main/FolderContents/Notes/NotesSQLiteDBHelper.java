package com.example.tapiwa.collegebuddy.Main.FolderContents.Notes;

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
    public static final String NOTE_COLOR_COLUMN = "cardColor";



    public NotesSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME , null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table personalNotes" +
                        "(id integer primary key, class text, title text,contents text, time text, cardColor text)"

                //// TODO: 7/9/17 check if text is the right type of text to be entered here
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        String upgradeQuery = "ALTER TABLE " + NOTES_TABLE_NAME + " ADD COLUMN " + NOTE_COLOR_COLUMN + " TEXT";
        if (oldVersion == 1 && newVersion == 2)
            db.execSQL(upgradeQuery);
    }

    public void insertNote (String classtype, String title, String contents,String time,String color){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("class", classtype);
        contentValues.put("title", title);
        contentValues.put("contents", contents);
        contentValues.put("time", time);
        contentValues.put(NOTE_COLOR_COLUMN, color);
        db.insert("personalNotes", null, contentValues);
    }

    public String getNoteContents(String classtype, String title) {

        // TODO: 7/6/17 fix displayNote
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from personalNotes", null);
        //// TODO: 7/10/17 look for a better way to implement this
        res.moveToFirst();
        while (res.isAfterLast() == false) {

            String f = res.getString(res.getColumnIndex(NOTES_COLUMN_TITLE));

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

    public String getNoteColor(String classtype, String title) {

        SQLiteDatabase db = this.getReadableDatabase();
        String [] args = {classtype, title};
        Cursor res = db.rawQuery("select * from personalNotes where class = ? and title = ?", args);

        //// TODO: 7/10/17 look for a better way to implement this
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if (res.getString(res.getColumnIndex(NOTES_COLUMN_TITLE)).equals(title)) {
                return res.getString(res.getColumnIndex(NOTE_COLOR_COLUMN));
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
    public boolean updateNote (String classtype, String title, String contents, String time, String color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("class", classtype);
        contentValues.put("title", title);
        contentValues.put("contents", contents);
        contentValues.put("time", time);
        contentValues.put(NOTE_COLOR_COLUMN, color);
        String [] whereArgs = {title};
        db.update("personalNotes", contentValues, "title = ? ", whereArgs);
        return true;
    }

   //// TODO: 7/6/17 fix this part
    public void deleteNote (String classname, String Notetitle) {
       // String givenNote = Notetitle;
        SQLiteDatabase db = this.getReadableDatabase();
        String [] whereArgs = {classname, Notetitle};
        db.delete("personalNotes", "class = ? and title = ?", whereArgs);
    }

    public ArrayList<String> searchNote(String className, String Notetitle) {

        ArrayList<String> array_list = new ArrayList<String>();
        String [] args = {className, Notetitle + '%'};
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase
                .rawQuery("select * from personalNotes where class = ? and title like ?", args);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(NOTES_COLUMN_TITLE)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllTitles(String classType) {

        ArrayList<String> array_list = new ArrayList<String>();


            SQLiteDatabase db = this.getReadableDatabase();
            String[] args = {classType};
            Cursor res = db.rawQuery("select * from personalNotes where class = ?", args);
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                String note = res.getString(res.getColumnIndex(NOTES_COLUMN_TITLE));

                array_list.add(res.getString(res.getColumnIndex(NOTES_COLUMN_TITLE)));
                res.moveToNext();
            }
        return array_list;
    }

}
